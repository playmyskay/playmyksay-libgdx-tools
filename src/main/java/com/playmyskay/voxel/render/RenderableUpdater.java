package com.playmyskay.voxel.render;

public class RenderableUpdater implements IRenderableHandler {
	private ChunkModelBatch chunkModelBatch;

	public RenderableUpdater(ChunkModelBatch chunkModelBatch) {
		this.chunkModelBatch = chunkModelBatch;
	}

	@Override
	public void update (RenderUpdateData updateData) {
		while (!chunkModelBatch.addUpdateData(updateData))
			;
	}

}
