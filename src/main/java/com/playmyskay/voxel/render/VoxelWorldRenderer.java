package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.playmyskay.voxel.render.RenderManager.UpdateType;

public class VoxelWorldRenderer {
	private float updateRenderablesTime = 0f;
	private ChunkModelBatch chunkModelBatch = new ChunkModelBatch();
	private RenderManager renderManager = new RenderManager();
	private Camera camera;

	public VoxelWorldRenderer() {
		renderManager.renderableHandler( (updateType, chunkRenderable) -> {
			RenderChange renderChange = null;
			if (updateType == UpdateType.add) {
				renderChange = new RenderChange(RenderChange.Type.create);
			} else if (updateType == UpdateType.remove) {
				renderChange = new RenderChange(RenderChange.Type.remove);
			}

			if (renderChange != null) {
				renderChange.setRenderableItems(chunkRenderable.renderableItemList);
				chunkModelBatch.renderQueue().add(renderChange);
			}
		});
	}

	public void camera (Camera camera) {
		this.camera = camera;
	}

	public void environment (Environment environment) {
		chunkModelBatch.environment(environment);
	}

	public ChunkModelBatch batch () {
		return chunkModelBatch;
	}

	public RenderManager renderManager () {
		return renderManager;
	}

	public void render (float deltaTime) {
		updateRenderablesTime += deltaTime;
		if (updateRenderablesTime >= 0.5f) {
//			if (future == null || future.isDone()) {
//				future = ThreadPool.getInstance().executor().submit(updateRenderableRunnable);
//			}
			try {
				renderManager.updateChunks();
				renderManager.sync();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			updateRenderablesTime = 0f;
		}

		chunkModelBatch.begin(camera);
		chunkModelBatch.render();
		chunkModelBatch.end();
	}
}
