package com.playmyskay.voxel.world;

import java.util.concurrent.Future;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.voxel.common.VoxelNodeProvider;
import com.playmyskay.voxel.common.VoxelOctree;
import com.playmyskay.voxel.common.VoxelOctreeProvider;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.processing.JobProcessor;
import com.playmyskay.voxel.render.VoxelWorldRenderer;
import com.playmyskay.voxel.type.IVoxelTypeProvider;

public class VoxelWorld {

	public VoxelOctree voxelOctree;
	public ChunkManager chunkManager = new ChunkManager(this);
	public VoxelWorldRenderer worldRenderer;
	public Vector3 viewerPosition = new Vector3();
	public BoundingBox visibilityBoundingBox = new BoundingBox(new Vector3(0f, 0f, 0f), new Vector3(128f, 128f, 128f));
	public BoundingBox cachingBoundingBox = new BoundingBox(new Vector3(0f, 0f, 0f), new Vector3(1024f, 1024f, 1024f));

	private class UpdateRunnable implements Runnable {
		private Vector3 position = new Vector3();
		private Vector3 positionLast = new Vector3();
		private Vector3 min = new Vector3();
		private Vector3 max = new Vector3();
		private float updateAccu = 0f;

//		public void setPosition (Vector3 position) {
//			this.position = position;
//		}

		public boolean needUpdate () {
			worldProvider.getViewerPostion(position);
			if (!(positionLast.idt(position))) {
				updateAccu = 0f;
				return true;
			}

//			updateAccu += Gdx.graphics.getDeltaTime();
//			if (updateAccu < 1f) {
//				return true;
//			}

			return true;
		}

		@Override
		public void run () {
			int visible_width = (int) (visible_chunk_width / 2) * VoxelWorld.CHUNK_SIZE;
			int visible_height = (int) (visible_chunk_height) * VoxelWorld.CHUNK_SIZE;
			int visible_depth = (int) (visible_chunk_depth / 2) * VoxelWorld.CHUNK_SIZE;
			min.set(position.x - visible_width, 0, position.z - visible_depth);
			max.set(position.x + visible_width, visible_height, position.z + visible_depth);
			getVisibilityBoundingBox().set(min, max);

			int cached_width = (int) (cached_chunk_width / 2) * VoxelWorld.CHUNK_SIZE;
			int cached_height = (int) (cached_chunk_height) * VoxelWorld.CHUNK_SIZE;
			int cached_depth = (int) (cached_chunk_depth / 2) * VoxelWorld.CHUNK_SIZE;
			min.set(position.x - cached_width, 0, position.z - cached_depth);
			max.set(position.x + cached_width, cached_height, position.z + cached_depth);
			getCachedBoundingBox().set(min, max);

			chunkManager.updateVisibleChunks();

			positionLast.set(position);
		}
	}

	private UpdateRunnable updateRunnable;
	private Future<?> updateFuture;

	public float update_tick = 0.5f;
	public IVoxelWorldProvider worldProvider;
	public IVoxelTypeProvider typeProvider;
	public int visible_chunk_width = 32;
	public int visible_chunk_height = 4;
	public int visible_chunk_depth = 32;
	public int cached_chunk_width = 32;
	public int cached_chunk_height = 4;
	public int cached_chunk_depth = 32;

	public static int CHUNK_SIZE = 16;
	public static int CHUNK_DIM = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

	public static VoxelWorld create (IVoxelWorldProvider worldProvider, IVoxelTypeProvider typeProvider) {
		return new VoxelWorld(worldProvider, typeProvider);
	}

	private VoxelWorld(IVoxelWorldProvider worldProvider, IVoxelTypeProvider typeProvider) {
		this.worldProvider = worldProvider;
		this.typeProvider = typeProvider;
		this.voxelOctree = new VoxelOctree(new VoxelNodeProvider(this));
		this.worldRenderer = new VoxelWorldRenderer(this);
		this.updateRunnable = new UpdateRunnable();

		VoxelOctreeProvider.set(voxelOctree);
//		Runnable r = new Runnable() {
//			@Override
//			public void run () {
//				createWorld();
//			}
//		};
//		new Thread(r).start();
	}

	public BoundingBox getVisibilityBoundingBox () {
		return visibilityBoundingBox;
	}

	public BoundingBox getCachedBoundingBox () {
		return cachingBoundingBox;
	}

	private float updateDeltaAccu = 0f;

	public void startThread () {

	}

	public void update () {
		updateDeltaAccu += Gdx.graphics.getDeltaTime();
		if (updateDeltaAccu >= update_tick) {
			// only one update thread should be running
			if (updateRunnable.needUpdate()) {
				if (updateFuture != null) {
					if (updateFuture.isDone()) {
						updateFuture = null;
					}
				}
				if (updateFuture == null) {
					updateFuture = JobProcessor.add(updateRunnable);
					updateDeltaAccu = 0f;
				}
			}

		}
	}

	public void setVoxel (Vector3 v, VoxelDescriptor descriptor) {
		voxelOctree.setNode(v, descriptor);
	}

	public IVoxelTypeProvider typeProvider () {
		return typeProvider;
	}
}
