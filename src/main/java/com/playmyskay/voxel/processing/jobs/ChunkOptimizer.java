package com.playmyskay.voxel.processing.jobs;

import java.util.ArrayList;
import java.util.List;

import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class ChunkOptimizer implements Runnable {

	private VoxelLevelChunk chunk;
	private List<VoxelComposite> voxelCompositeList;

	public void addEntity (VoxelComposite voxelComposite) {
		if (voxelCompositeList == null) voxelCompositeList = new ArrayList<>();
		voxelCompositeList.add(voxelComposite);
	}

	public ChunkOptimizer(VoxelLevelChunk chunk) {
		this.chunk = chunk;
	}

	@Override
	public void run () {
		for (VoxelComposite voxelComposite : voxelCompositeList) {
		}

	}

}
