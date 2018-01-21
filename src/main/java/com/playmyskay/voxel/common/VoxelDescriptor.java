package com.playmyskay.voxel.common;

import com.playmyskay.octree.OctreeNodeDescriptor;
import com.playmyskay.voxel.type.VoxelTypeDescriptor;

public class VoxelDescriptor extends OctreeNodeDescriptor {
	public VoxelTypeDescriptor voxelTypeDescriptor = new VoxelTypeDescriptor();
	public boolean updateInstant = false;

	public void reset () {
		voxelTypeDescriptor.reset();
		updateInstant = false;
	}

}
