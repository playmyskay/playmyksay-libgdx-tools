package com.playmyskay.voxel.render;

import com.playmyskay.voxel.level.VoxelLevelChunk;

public class ChunkRenderable {
	public VoxelLevelChunk voxelLevelChunk;
	public RenderableData renderableData;

	public void reset () {
		voxelLevelChunk = null;
		renderableData = null;
	}
}
