package com.playmyskay.voxel.type;

import com.playmyskay.voxel.look.VoxelLook;

public class VoxelTypeDescriptor {

	public VoxelType voxelType = VoxelType.undef;
	public VoxelLook voxelLook;

	public VoxelTypeDescriptor() {

	}

	public boolean equal (final VoxelTypeDescriptor voxelTypeDescriptor) {
		if (this.voxelType != voxelTypeDescriptor.voxelType) {
			return false;
		}
		if (this.voxelLook != voxelTypeDescriptor.voxelLook) {
			return false;
		}
		return true;
	}

	public VoxelTypeDescriptor copy () {
		VoxelTypeDescriptor voxelTypeDescriptor = new VoxelTypeDescriptor();
		voxelTypeDescriptor.voxelLook = voxelLook;
		voxelTypeDescriptor.voxelType = voxelType;
		return voxelTypeDescriptor;
	}

	public void reset () {
		voxelType = VoxelType.undef;
	}
}
