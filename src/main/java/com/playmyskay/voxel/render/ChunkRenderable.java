package com.playmyskay.voxel.render;

import java.util.ArrayList;
import java.util.List;

import com.playmyskay.voxel.common.VoxelOffset;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class ChunkRenderable {
	public VoxelLevelChunk voxelLevelChunk;
	public VoxelOffset voxelOffset = new VoxelOffset();

	public List<RenderableItem> renderableItemList = new ArrayList<RenderableItem>();

	public void reset () {
		voxelLevelChunk = null;
	}

	public void setOffset (final VoxelOffset voxelOffset) {
		this.voxelOffset.set(voxelOffset);
	}
}
