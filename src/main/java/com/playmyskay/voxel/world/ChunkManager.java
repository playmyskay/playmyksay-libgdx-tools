package com.playmyskay.voxel.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.playmyskay.octree.common.OctreeCalc;
import com.playmyskay.octree.common.OctreeCalcPoolManager;
import com.playmyskay.octree.common.OctreeNodeDescriptor.BaseActionType;
import com.playmyskay.octree.common.OctreeNodeTools;
import com.playmyskay.octree.common.OctreeTools;
import com.playmyskay.octree.common.OctreeTools.INodeHandler;
import com.playmyskay.voxel.common.VoxelNodeProvider;
import com.playmyskay.voxel.common.descriptors.AddVoxelDescriptor;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.look.VoxelLookType;
import com.playmyskay.voxel.processing.JobProcessor;
import com.playmyskay.voxel.render.UpdateType;
import com.playmyskay.voxel.world.IVoxelWorldProvider.WorldData;

public class ChunkManager {
	private Map<Vector3, VoxelLevelChunk> cachedChunkSet;
	private Map<Vector3, VoxelLevelChunk> visibleChunkSet;
	private Map<Vector3, VoxelLevelChunk> tmpChunkSet;
	private List<IChunkUpdateListener> updateListeners = new ArrayList<>();
	private VoxelWorld voxelWorld;
	private VolumePool volumePool = new VolumePool();
	private FacePool facePool = new FacePool();
//	private Pool<Vector3> vectorPool = new Pool<Vector3>() {
//
//		@Override
//		protected Vector3 newObject () {
//			return new Vector3();
//		}
//
//	};

	public ChunkManager(VoxelWorld voxelWorld) {
		this.voxelWorld = voxelWorld;

		int capacity = voxelWorld.cached_chunk_width * voxelWorld.cached_chunk_depth * voxelWorld.cached_chunk_height;
		this.cachedChunkSet = new HashMap<>(capacity);
		this.visibleChunkSet = new HashMap<>(capacity);
		this.tmpChunkSet = new HashMap<>(capacity);
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
		offset.x = offset.x - offset.x % VoxelWorld.CHUNK_SIZE;
		offset.y = offset.y - offset.y % VoxelWorld.CHUNK_SIZE;
		offset.z = offset.z - offset.z % VoxelWorld.CHUNK_SIZE;

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

		chunkHandler.finish();
	}

	private interface IChunkHandler {
		void handle (int chunk_pos_x, int chunk_pos_y, int chunk_pos_z);

		void finish ();
	}

	private void handleCachedChunks (final VoxelWorld world, Map<Vector3, VoxelLevelChunk> tmpChunkSet,
			Map<Vector3, VoxelLevelChunk> cachedChunkSet) {
		tmpChunkSet.clear();

		AddVoxelDescriptor descriptor = new AddVoxelDescriptor();
		descriptor.updateInstant = false;

		VoxelDescriptor tmpDescriptor = descriptor.copy();
		Map<VoxelLookType, VoxelDescriptor> lookDescriptorMap = new HashMap<>();

		// Grass
		tmpDescriptor = descriptor.copy();
		tmpDescriptor.voxelTypeDescriptor.lookType = VoxelLookType.Grass;
		lookDescriptorMap.put(VoxelLookType.Grass, tmpDescriptor);

		// Water
		tmpDescriptor = descriptor.copy();
		tmpDescriptor.voxelTypeDescriptor.lookType = VoxelLookType.Water;
		lookDescriptorMap.put(VoxelLookType.Water, tmpDescriptor);

		// Sand
		tmpDescriptor = descriptor.copy();
		tmpDescriptor.voxelTypeDescriptor.lookType = VoxelLookType.Sand;
		lookDescriptorMap.put(VoxelLookType.Sand, tmpDescriptor);

		List<Future<?>> futures = new ArrayList<>();
		handleBounds(world.getCachedBoundingBox(), new IChunkHandler() {
			private OctreeCalc calc = OctreeCalcPoolManager.obtain();
			private Vector3 searchVector = calc.vector();
			private Vector3 min = new Vector3();
			private Vector3 max = new Vector3();

			@Override
			public void handle (int chunk_pos_x, int chunk_pos_y, int chunk_pos_z) {
				if (world.voxelOctree != calc.octree()) {
					calc.octree(world.voxelOctree);
				}

				VoxelLevelChunk chunk = searchChunk(cachedChunkSet, chunk_pos_x, chunk_pos_y, chunk_pos_z,
						searchVector);
				if (chunk == null || !chunk.valid()) {
					chunk = new VoxelLevelChunk();
					chunk.boundingBox().set(min.set(chunk_pos_x, chunk_pos_y, chunk_pos_z),
							max.set(chunk_pos_x + VoxelWorld.CHUNK_SIZE, chunk_pos_y + VoxelWorld.CHUNK_SIZE,
									chunk_pos_z + VoxelWorld.CHUNK_SIZE));
				}
				tmpChunkSet.put(chunk.boundingBox().min, chunk);

				final VoxelLevelChunk chunk2 = chunk;
				if (!cachedChunkSet.containsKey(chunk.boundingBox().min)) {
					futures.add(JobProcessor.add(new Runnable() {
						@Override
						public void run () {
							OctreeCalc calc2 = OctreeCalcPoolManager.obtain();
							calc2.octree(calc.octree());
							VoxelLevelEntity[][][] volume = volumePool.obtain();
							byte[][][] faces = facePool.obtain();
							createChunk2(world, chunk2, volume, chunk_pos_x, chunk_pos_y, chunk_pos_z,
									lookDescriptorMap, calc2);
							chunk2.rebuild(volume, faces);
							OctreeCalcPoolManager.free(calc2);
							volumePool.free(volume);
							facePool.free(faces);
						}
					}));
				}
			}

			@Override
			public void finish () {
				OctreeCalcPoolManager.free(calc);
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

		INodeHandler<VoxelLevel> nodeHandler = new INodeHandler<VoxelLevel>() {
			@Override
			public void process (VoxelLevel node) {
				((VoxelNodeProvider) world.voxelOctree.nodeProvider).free(node);
			}

		};

		for (Map.Entry<Vector3, VoxelLevelChunk> entry : cachedChunkSet.entrySet()) {
			if (!tmpChunkSet.containsKey(entry.getKey())) {
				OctreeTools.removeNode(entry.getValue(), nodeHandler);
			}
		}

		cachedChunkSet.clear();
		for (Map.Entry<Vector3, VoxelLevelChunk> entry : tmpChunkSet.entrySet()) {
			if (!entry.getValue().valid()) continue;
			cachedChunkSet.put(entry.getKey(), entry.getValue());
		}
	}

	private static void handleVisibleChunks (VoxelWorld world, Map<Vector3, VoxelLevelChunk> tmpChunkSet,
			Map<Vector3, VoxelLevelChunk> visibleChunkSet, Map<Vector3, VoxelLevelChunk> cachedChunkSet,
			ChunkManager chunkManager) {
		tmpChunkSet.clear();
		List<Future<?>> futures = new ArrayList<>();
		handleBounds(world.getVisibilityBoundingBox(), new IChunkHandler() {
			private Vector3 searchVector = new Vector3();

			@Override
			public void handle (int chunk_pos_x, int chunk_pos_y, int chunk_pos_z) {
				VoxelLevelChunk chunk = searchChunk(cachedChunkSet, chunk_pos_x, chunk_pos_y, chunk_pos_z,
						searchVector);
				if (chunk != null && chunk.valid()) {
					tmpChunkSet.put(chunk.boundingBox().min, chunk);

					final VoxelLevelChunk chunk2 = chunk;
					if (chunk.valid() && !visibleChunkSet.containsKey(chunk.boundingBox().min)) {
						futures.add(JobProcessor.add(new Runnable() {
							@Override
							public void run () {
								chunkManager.updateListeners(UpdateType.addChunk, chunk2);
							}
						}));
					}
				}
			}

			@Override
			public void finish () {

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

		for (Map.Entry<Vector3, VoxelLevelChunk> entry : visibleChunkSet.entrySet()) {
			if (!tmpChunkSet.containsKey(entry.getKey())) {
				chunkManager.updateListeners(UpdateType.removeChunk, entry.getValue());
			}
		}

		visibleChunkSet.clear();
		for (Map.Entry<Vector3, VoxelLevelChunk> entry : tmpChunkSet.entrySet()) {
			if (!entry.getValue().valid()) continue;
			visibleChunkSet.put(entry.getKey(), entry.getValue());
		}
	}

	private void updateOctree () {
		handleCachedChunks(voxelWorld, tmpChunkSet, cachedChunkSet);
		handleVisibleChunks(voxelWorld, tmpChunkSet, visibleChunkSet, cachedChunkSet, this);
	}

	private static VoxelLevelChunk searchChunk (Map<Vector3, VoxelLevelChunk> map, int chunk_pos_x, int chunk_pos_y,
			int chunk_pos_z, Vector3 searchVector) {
		searchVector.set(chunk_pos_x, chunk_pos_y, chunk_pos_z);
		VoxelLevelChunk chunk = map.get(searchVector);
		return chunk;
	}

	private static void createChunk (VoxelWorld world, VoxelLevelChunk chunk, int offset_x, int offset_y, int offset_z,
			Map<VoxelLookType, VoxelDescriptor> lookDescriptorMap, OctreeCalc calc) {
		float cur_pos_x = 0f;
		float cur_pos_y = 0f;
		float cur_pos_z = 0f;

//		LevelIndexer levelIndexer = new LevelIndexer();
//		levelIndexer.nodeArry = new Array<OctreeNode<?>>(3);
//		calc.levelIndexer();

		Vector3 v = new Vector3();
		VoxelDescriptor grassDescriptor = lookDescriptorMap.get(VoxelLookType.Grass);
		VoxelDescriptor waterDescriptor = lookDescriptorMap.get(VoxelLookType.Water);
		VoxelDescriptor sandDescriptor = lookDescriptorMap.get(VoxelLookType.Sand);
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					cur_pos_x = (offset_x + x);
					cur_pos_y = (offset_y + y);
					cur_pos_z = (offset_z + z);
					if (world.worldProvider.get(cur_pos_x, cur_pos_y, cur_pos_z)) {
						v.set(offset_x + x, offset_y + y, offset_z + z);
//						if (Logger.get() != null) {
////							Logger.get().log(String.format("x%.0f y%.0f z%.0f", v.x, v.y, v.z));
//						}

						if (v.y >= 0 && v.y < 18f) {
							OctreeNodeTools.addNodeByVector(world.voxelOctree.nodeProvider, chunk, v, sandDescriptor,
									calc);
						} else {
							OctreeNodeTools.addNodeByVector(world.voxelOctree.nodeProvider, chunk, v, grassDescriptor,
									calc);
						}
					} else {
						v.set(offset_x + x, offset_y + y, offset_z + z);
						if (v.y <= 16f) {
							OctreeNodeTools.addNodeByVector(world.voxelOctree.nodeProvider, chunk, v, waterDescriptor,
									calc);
						}
					}
				}
			}
		}

		calc.reset();
		world.voxelOctree.addNode(chunk, BaseActionType.add, calc);
		chunk.valid(true);
	}

	private static void createChunk3 (VoxelWorld world, VoxelLevelChunk chunk, int level, int offset_x, int offset_y,
			int offset_z, Map<VoxelLookType, VoxelDescriptor> lookDescriptorMap, OctreeCalc calc) {

		float cur_pos_x = 0f;
		float cur_pos_y = 0f;
		float cur_pos_z = 0f;

//		LevelIndexer levelIndexer = new LevelIndexer();
//		levelIndexer.nodeArry = new Array<OctreeNode<?>>(3);
//		calc.levelIndexer();

		Vector3 v = new Vector3();
		VoxelDescriptor grassDescriptor = lookDescriptorMap.get(VoxelLookType.Grass);
		VoxelDescriptor waterDescriptor = lookDescriptorMap.get(VoxelLookType.Water);
		VoxelDescriptor sandDescriptor = lookDescriptorMap.get(VoxelLookType.Sand);

		VoxelLevelEntity[][][] volume = new VoxelLevelEntity[VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE];
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					cur_pos_x = (offset_x + x);
					cur_pos_y = (offset_y + y);
					cur_pos_z = (offset_z + z);

					if (world.worldProvider.get(cur_pos_x, cur_pos_y, cur_pos_z)) {
						volume[x][y][z] = new VoxelLevelEntity();
						volume[x][y][z].descriptor = grassDescriptor.voxelTypeDescriptor;
					}
				}
			}
		}
	}

	private static void createChunk2 (VoxelWorld world, VoxelLevelChunk chunk, VoxelLevelEntity[][][] volume,
			int worldPosition_x, int worldPosition_y, int worldPosition_z,
			Map<VoxelLookType, VoxelDescriptor> lookDescriptorMap, OctreeCalc calc) {
//		LevelIndexer levelIndexer = new LevelIndexer();
//		levelIndexer.nodeArry = new Array<OctreeNode<?>>(3);
//		calc.levelIndexer();

		int chunkLevelIndex = world.voxelOctree.nodeProvider.levelIndex(VoxelLevelChunk.class);
		int[] levelDivider = new int[chunkLevelIndex + 1];
		int divide = 0;
		for (int i = 1; i <= chunkLevelIndex; ++i) {
			switch (i) {
			case 6:
				divide = VoxelWorld.CHUNK_SIZE / 32;
				break;
			case 5:
				divide = VoxelWorld.CHUNK_SIZE / 16;
				break;
			case 4:
				divide = VoxelWorld.CHUNK_SIZE / 8;
				break;
			case 3:
				divide = VoxelWorld.CHUNK_SIZE / 4;
				break;
			case 2:
				divide = VoxelWorld.CHUNK_SIZE / 2;
				break;
			case 1:
				divide = VoxelWorld.CHUNK_SIZE;
				break;
			default:
				throw new GdxRuntimeException("level not supported");
			}
			levelDivider[i] = divide;
		}

		WorldData worldData = new WorldData();
		processChildNode(world, worldData, volume, chunk, chunkLevelIndex, levelDivider, worldPosition_x,
				worldPosition_y, worldPosition_z, 0, 0, 0, lookDescriptorMap);

		calc.reset();
		world.voxelOctree.addNode(chunk, BaseActionType.add, calc);
		chunk.valid(true);
	}

	private static VoxelLevel processChildNode (VoxelWorld world, WorldData worldData, VoxelLevelEntity[][][] volume,
			VoxelLevel parentNode, int level, int[] levelDivider, int worldPosition_x, int worldPosition_y,
			int worldPosition_z, int offset_x, int offset_y, int offset_z,
			Map<VoxelLookType, VoxelDescriptor> lookDescriptorMap) {

		VoxelLevel childNode = null;
		int index = 0;
		int calc_offset_x = 0;
		int calc_offset_y = 0;
		int calc_offset_z = 0;
		for (int y = 0; y < 2; ++y) {
			for (int z = 0; z < 2; ++z) {
				for (int x = 0; x < 2; ++x) {
					calc_offset_x = offset_x + (x == 1 ? (VoxelWorld.CHUNK_SIZE / levelDivider[level]) : 0);
					calc_offset_y = offset_y + (y == 1 ? (VoxelWorld.CHUNK_SIZE / levelDivider[level]) : 0);
					calc_offset_z = offset_z + (z == 1 ? (VoxelWorld.CHUNK_SIZE / levelDivider[level]) : 0);

					if (level > 1) {
						childNode = processChildNode(world, worldData, volume, null, level - 1, levelDivider,
								worldPosition_x, worldPosition_y, worldPosition_z, calc_offset_x, calc_offset_y,
								calc_offset_z, lookDescriptorMap);
					} else if (level == 1) {
						childNode = createEntityLevel(world, worldData, volume, worldPosition_x, worldPosition_y,
								worldPosition_z, calc_offset_x, calc_offset_y, calc_offset_z, lookDescriptorMap);
					}

					if (childNode != null) {
						if (parentNode == null) {
							parentNode = world.voxelOctree.nodeProvider.create(level);
						}
						childNode.parent(parentNode);

						if (parentNode.childs() == null) {
							parentNode.childs(world.voxelOctree.nodeProvider.createArray(level - 1, 8));
						}
						parentNode.child(index, childNode);
					}

					++index;
				}
			}
		}

		return parentNode;
	}

	private static VoxelLevel createEntityLevel (VoxelWorld world, WorldData worldData, VoxelLevelEntity[][][] volume,
			int worldPosition_x, int worldPosition_y, int worldPosition_z, int offset_x, int offset_y, int offset_z,
			Map<VoxelLookType, VoxelDescriptor> lookDescriptorMap) {
		worldData.x = worldPosition_x + offset_x;
		worldData.y = worldPosition_y + offset_y;
		worldData.z = worldPosition_z + offset_z;
		if (world.worldProvider.get(worldData)) {
			VoxelLevelEntity entity = (VoxelLevelEntity) world.voxelOctree.nodeProvider.create(0);
			entity.descriptor = lookDescriptorMap.get(VoxelLookType.Grass).voxelTypeDescriptor;
			volume[offset_x][offset_y][offset_z] = entity;
			return entity;
		} else {
			volume[offset_x][offset_y][offset_z] = null;
		}
		return null;
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
