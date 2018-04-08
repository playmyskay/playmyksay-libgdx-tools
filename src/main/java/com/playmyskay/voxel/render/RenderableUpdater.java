package com.playmyskay.voxel.render;

public class RenderableUpdater implements RenderableHandler {
	private ChunkModelBatch chunkModelBatch;

	public RenderableUpdater(ChunkModelBatch chunkModelBatch) {
		this.chunkModelBatch = chunkModelBatch;
	}

	@Override
	public void update (ChunkRenderable chunkRenderable) {
		chunkModelBatch.renderQueue().offer(chunkRenderable);
	}

}
