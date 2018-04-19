package com.playmyskay.voxel.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.common.OctreeCalc;
import com.playmyskay.octree.common.OctreeNodeTools;
import com.playmyskay.octree.common.OctreeTools;
import com.playmyskay.voxel.common.descriptors.AddVoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.processing.JobProcessor;
import com.playmyskay.voxel.render.UpdateType;

public class ChunkManager {
	private Set<VoxelLevelChunk> cachedChunkSet = new HashSet<>();
	private Set<VoxelLevelChunk> visibleChunkSet = new HashSet<>();
	private Set<VoxelLevelChunk> tmpChunkSet = new HashSet<>();
	private List<IChunkUpdateListener> updateListeners = new ArrayList<>();
	private VoxelWorld voxelWorld;

	public ChunkManager(VoxelWorld voxelWorld) {
		this.voxelWorld = voxelWorld;
	}

	public void updateVisibleChunks () {
		updateOctree();
	}

	private static void handleBounds (BoundingBox bounds, IChunkHandler chunkHandler) {
		int width = (int) (bounds.getWidth() / VoxelWorld.CHUNK_SIZE);
		int height = (int) (bounds.getHeight() / VoxelWorld.CHUNK_SIZE);
		int depth = (int) (bounds.getDepth() / VoxelWorld.CHUNK_SIZE);

		System.out.printf("updateOctree: w(%d) h(%d) d(%d) -> %d\n", width, height, depth, width * height * depth);

		Vector3 offset = bounds.getMin(new Vector3());
		offset.x = offset.x - offset.x % 16;
		offset.y = offset.y - offset.y % 16;
		offset.z = offset.z - offset.z % 16;

		int chunk_pos_x = 0;
		int chunk_pos_y = 0;
		int chunk_pos_z = 0;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < depth; ++z) {
					chunk_pos_x = (int) (offset.x + x * VoxelWorld.CHUNK_SIZE);
					chunk_pos_y = (int) (offset.y + y * VoxelWorld.CHUNK_SIZE);
					chunk_pos_z = (int) (offset.z + z * VoxelWorld.CHUNK_SIZE);

					chunkHandler.handle(chunk_pos_x, chunk_pos_y, chunk_pos_z);
				}
			}
		}
	}

	private static interface IChunkHandler {
		void handle (int chunk_pos_x, int chunk_pos_y, int chunk_pos_z);
	}

	private static void handleCachedChunks (VoxelWorld world, Set<VoxelLevelChunk> tmpChunkSet,
			Set<VoxelLevelChunk> cachedChunkSet) {
		tmpChunkSet.clear();

		AddVoxelDescriptor descriptor = new AddVoxelDescriptor();
		descriptor.updateInstant = false;

		List<Future<?>> futures = new ArrayList<>();
		handleBounds(world.getCachedBoundingBox(), new IChunkHandler() {
			private OctreeCalc calc = new OctreeCalc();

			@Override
			public void handle (int chunk_pos_x, int chunk_pos_y, int chunk_pos_z) {
				VoxelLevelChunk chunk = searchChunk(cachedChunkSet, chunk_pos_x, chunk_pos_y, chunk_pos_z);
				if (chunk == null || !chunk.valid()) {
					chunk = createChunk(world, chunk_pos_x, chunk_pos_y, chunk_pos_z, descriptor, calc);
				}
				tmpChunkSet.add(chunk);

				final VoxelLevelChunk chunk2 = chunk;
				if (chunk.valid() && !cachedChunkSet.contains(chunk)) {
					futures.add(JobProcessor.add(new Runnable() {
						@Override
						public void run () {
							chunk2.rebuild();
						}
					}));
				}
			}
		});

		for (Future<?> future : futures) {
			try {
				while (!future.isDone())
					Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		cachedChunkSet.forEach(chunk -> {
			if (!tmpChunkSet.contains(chunk)) {
				OctreeTools.removeNode(chunk);
			}
		});

		cachedChunkSet.clear();
		for (VoxelLevelChunk chunk : tmpChunkSet) {
			if (!chunk.valid()) continue;
			cachedChunkSet.add(chunk);
		}
	}

	private static void handleVisibleChunks (VoxelWorld world, Set<VoxelLevelChunk> tmpChunkSet,
			Set<VoxelLevelChunk> visibleChunkSet, Set<VoxelLevelChunk> cachedChunkSet, ChunkManager chunkManager) {
		tmpChunkSet.clear();
		List<Future<?>> futures = new ArrayList<>();
		handleBounds(world.getVisibilityBoundingBox(), new IChunkHandler() {

			@Override
			public void handle (int chunk_pos_x, int chunk_pos_y, int chunk_pos_z) {
				VoxelLevelChunk chunk = searchChunk(cachedChunkSet, chunk_pos_x, chunk_pos_y, chunk_pos_z);
				if (chunk != null && chunk.valid()) {
					tmpChunkSet.add(chunk);

					final VoxelLevelChunk chunk2 = chunk;
					if (chunk.valid() && !visibleChunkSet.contains(chunk)) {
						futures.add(JobProcessor.add(new Runnable() {
							@Override
							public void run () {
								chunkManager.updateListeners(UpdateType.addChunk, chunk2);
							}
						}));
					}
				}
			}
		});

		for (Future<?> future : futures) {
			try {
				while (!future.isDone())
					Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		visibleChunkSet.forEach(chunk -> {
			if (!tmpChunkSet.contains(chunk)) {
				chunkManager.updateListeners(UpdateType.removeChunk, chunk);
			}
		});

		visibleChunkSet.clear();
		for (VoxelLevelChunk chunk : tmpChunkSet) {
			if (!chunk.valid()) continue;
			visibleChunkSet.add(chunk);
		}
	}

	private void updateOctree () {
		handleCachedChunks(voxelWorld, tmpChunkSet, cachedChunkSet);
		handleVisibleChunks(voxelWorld, tmpChunkSet, visibleChunkSet, cachedChunkSet, this);
	}

	private static VoxelLevelChunk searchChunk (Set<VoxelLevelChunk> set, int chunk_pos_x, int chunk_pos_y,
			int chunk_pos_z) {
		for (VoxelLevelChunk chunk : set) {
			if (chunk.boundingBox().min.idt(new Vector3(chunk_pos_x, chunk_pos_y, chunk_pos_z))) {
				return chunk;
			}
		}
		return null;
	}

	private static VoxelLevelChunk createChunk (VoxelWorld world, int offset_x, int offset_y, int offset_z,
			AddVoxelDescriptor descriptor, OctreeCalc calc) {
		float cur_pos_x = 0f;
		float cur_pos_y = 0f;
		float cur_pos_z = 0f;

		VoxelLevelChunk chunk = new VoxelLevelChunk();
		chunk.boundingBox().set(new Vector3(offset_x, offset_y, offset_z), new Vector3(offset_x + VoxelWorld.CHUNK_SIZE,
				offset_y + VoxelWorld.CHUNK_SIZE, offset_z + VoxelWorld.CHUNK_SIZE));

		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					cur_pos_x = (offset_x + x);
					cur_pos_y = (offset_y + y);
					cur_pos_z = (offset_z + z);
					if (world.worldProvider.get(cur_pos_x, cur_pos_y, cur_pos_z)) {
						Vector3 v = new Vector3();
						v.set(offset_x + x, offset_y + y, offset_z + z);
//						if (Logger.get() != null) {
////							Logger.get().log(String.format("x%.0f y%.0f z%.0f", v.x, v.y, v.z));
//						}
						OctreeNodeTools.addNodeByVector(world.voxelOctree.nodeProvider, chunk, v, descriptor, calc);
					}
				}
			}
		}

		world.voxelOctree.addNode(chunk, descriptor, calc);
		chunk.valid(true);

		return chunk;
	}

	public void addUpdateListener (IChunkUpdateListener listener) {
		updateListeners.add(listener);
	}

	public void updateListeners (UpdateType updateType, VoxelLevelChunk chunk) {
		for (IChunkUpdateListener listener : updateListeners) {
			UpdateData updateData = listener.create();
			updateData.type = updateType;
			updateData.voxelWorld = voxelWorld;
			updateData.voxelLevelChunk = chunk;
			listener.add(updateData);
		}
	}
}
