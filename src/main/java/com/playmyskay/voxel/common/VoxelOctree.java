package com.playmyskay.voxel.common;

import com.playmyskay.octree.common.Octree;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevel;

public class VoxelOctree extends Octree<VoxelLevel, VoxelDescriptor> {

	public VoxelOctree(VoxelNodeProvider provider) {
		setNodeProvider(provider);
	}

}
