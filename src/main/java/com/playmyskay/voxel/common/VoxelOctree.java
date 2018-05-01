package com.playmyskay.voxel.common;

import com.playmyskay.octree.common.Octree;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelOctree extends Octree<VoxelLevel, VoxelDescriptor> {

	public VoxelOctree(VoxelNodeProvider provider) {
		super(VoxelWorld.CHUNK_LEVEL + 1);
		setNodeProvider(provider);
	}

}
