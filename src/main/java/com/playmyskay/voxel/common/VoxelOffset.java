package com.playmyskay.voxel.common;

public class VoxelOffset {
	public int x = 0;
	public int y = 0;
	public int z = 0;

	public void set (VoxelOffset voxelOffset) {
		this.x = voxelOffset.x;
		this.y = voxelOffset.y;
		this.z = voxelOffset.z;
	}

	public VoxelOffset set (int offsetX, int offsetY, int offsetZ) {
		this.x = offsetX;
		this.y = offsetY;
		this.z = offsetZ;
		return this;
	}

	public void clear () {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
}
