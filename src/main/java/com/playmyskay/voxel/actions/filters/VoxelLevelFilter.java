package com.playmyskay.voxel.actions.filters;

import com.playmyskay.octree.traversal.IOctreeNodeFilter;
import com.playmyskay.voxel.level.VoxelLevel;

public abstract class VoxelLevelFilter implements IOctreeNodeFilter {

	@Override
	public boolean filter (Object node) {
		if (node != null && node instanceof VoxelLevel) {
			return filter((VoxelLevel) node);
		}
		return false;
	}

	protected abstract boolean filter (VoxelLevel voxelLevel);

}
