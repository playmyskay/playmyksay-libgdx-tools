package com.playmyskay.voxel.common.descriptors;

import com.playmyskay.octree.common.OctreeNodeDescriptor;
import com.playmyskay.voxel.type.VoxelTypeDescriptor;

public class VoxelDescriptor extends OctreeNodeDescriptor {
	public VoxelTypeDescriptor voxelTypeDescriptor = new VoxelTypeDescriptor();
	public boolean updateInstant = true;

	public VoxelDescriptor(BaseActionType baseActionType) {
		super(baseActionType);
	}

	public void reset () {
		voxelTypeDescriptor.reset();
		updateInstant = false;
	}

	public VoxelDescriptor copy () {
		VoxelDescriptor copyDescriptor = new VoxelDescriptor(this.getBaseActionType());
		copyDescriptor.updateInstant = this.updateInstant;
		copyDescriptor.voxelTypeDescriptor = voxelTypeDescriptor.copy();
		return copyDescriptor;
	}

}
