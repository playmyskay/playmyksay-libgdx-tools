package com.playmyskay.voxel.type;

import com.playmyskay.voxel.look.VoxelLookType;

public class VoxelTypeDescriptor {

	public VoxelUsageType voxelType = VoxelUsageType.undef;
	public VoxelLookType lookType = VoxelLookType.Grass;

	public VoxelTypeDescriptor() {

	}

	public boolean equal (final VoxelTypeDescriptor voxelTypeDescriptor) {
		if (this.voxelType != voxelTypeDescriptor.voxelType) {
			return false;
		}
		if (this.lookType != voxelTypeDescriptor.lookType) {
			return false;
		}
		return true;
	}

	public VoxelTypeDescriptor copy () {
		VoxelTypeDescriptor voxelTypeDescriptor = new VoxelTypeDescriptor();
		voxelTypeDescriptor.lookType = lookType;
		voxelTypeDescriptor.voxelType = voxelType;
		return voxelTypeDescriptor;
	}

	public void reset () {
		voxelType = VoxelUsageType.undef;
	}
}
