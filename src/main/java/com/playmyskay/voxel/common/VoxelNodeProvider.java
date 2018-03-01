package com.playmyskay.voxel.common;

import com.playmyskay.octree.common.IOctreeNodeProvider;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.level.VoxelLevelSpace;

public class VoxelNodeProvider implements IOctreeNodeProvider<VoxelLevel> {

	@Override
	public VoxelLevel create (int level) {
		if (level == 0) return new VoxelLevelEntity();
		if (level == 4) return new VoxelLevelChunk();
		return new VoxelLevelSpace();
	}

	@Override
	public VoxelLevel[] createArray (int level, int size) {
		return new VoxelLevel[size];
	}

	@Override
	public int depth (Class<?> clazz) {
		if (clazz.equals(VoxelLevelEntity.class)) return 0;
		if (clazz.equals(VoxelLevelChunk.class)) return 4;
		return -1;
	}

}