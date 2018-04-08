package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelWorldRenderer {
//	private float updateRenderablesTime = 0f;
	private ChunkModelBatch chunkModelBatch = new ChunkModelBatch();
	private RenderUpdateManager renderUpdateManager = new RenderUpdateManager(new RenderableUpdater(chunkModelBatch));
	@SuppressWarnings("unused")
	private WorldUpdateListener worldUpdateListener;
	private Camera camera;

	public VoxelWorldRenderer(VoxelWorld voxelWorld) {
		voxelWorld.voxelOctree.addListener(new WorldUpdateListener(renderUpdateManager));

		new Thread(new Runnable() {
			private ChunkManager chunkManager = new ChunkManager(voxelWorld, renderUpdateManager);

			@Override
			public void run () {
				while (true) {
					try {
						chunkManager.updateVisibleChunks();
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void camera (Camera camera) {
		this.camera = camera;
	}

	public void environment (Environment environment) {
		chunkModelBatch.environment(environment);
	}

	public ChunkModelBatch chunkModelBatch () {
		return chunkModelBatch;
	}

	public ChunkModelBatch batch () {
		return chunkModelBatch;
	}

	public void render (float deltaTime) {
//		updateRenderablesTime += deltaTime;

		chunkModelBatch.begin(camera);
		chunkModelBatch.render();
		chunkModelBatch.end();
	}
}
