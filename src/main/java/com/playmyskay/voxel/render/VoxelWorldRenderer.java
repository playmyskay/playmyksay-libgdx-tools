package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;

public class VoxelWorldRenderer {
	private float updateRenderablesTime = 0f;
	private ChunkModelBatch chunkModelBatch = new ChunkModelBatch();
	private RenderUpdateManager renderUpdateManager = new RenderUpdateManager(new RenderableUpdater(chunkModelBatch));
	private ChunkManager chunkManager = new ChunkManager(renderUpdateManager);
	@SuppressWarnings("unused")
	private WorldUpdateListener worldUpdateListener = new WorldUpdateListener(renderUpdateManager);
	private Camera camera;

	public VoxelWorldRenderer() {

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
		updateRenderablesTime += deltaTime;
		if (updateRenderablesTime >= 0.5f) {

			chunkManager.updateVisibleChunks();

//			if (future == null || future.isDone()) {
//				future = ThreadPool.getInstance().executor().submit(updateRenderableRunnable);
//			}

			updateRenderablesTime = 0f;
		}

		chunkModelBatch.begin(camera);
		chunkModelBatch.render();
		chunkModelBatch.end();
	}
}
