package com.playmyskay.voxel.render;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.playmyskay.voxel.common.VoxelOffset;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class ChunkRenderable {
	public VoxelLevelChunk voxelLevelChunk;
	public VoxelOffset voxelOffset = new VoxelOffset();
	public ConcurrentLinkedQueue<UpdateData> updateDataQueue = new ConcurrentLinkedQueue<UpdateData>();

	public void reset () {
		voxelLevelChunk = null;
	}

	public void setOffset (final VoxelOffset voxelOffset) {
		this.voxelOffset.set(voxelOffset);
	}
}
