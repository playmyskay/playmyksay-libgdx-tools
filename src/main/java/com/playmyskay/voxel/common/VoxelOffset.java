package com.playmyskay.voxel.common;

public class VoxelOffset {
	public float x = 0f;
	public float y = 0f;
	public float z = 0f;

	public void set (VoxelOffset voxelOffset) {
		this.x = voxelOffset.x;
		this.y = voxelOffset.y;
		this.z = voxelOffset.z;
	}

	public VoxelOffset set (float offsetX, float offsetY, float offsetZ) {
		this.x = offsetX;
		this.y = offsetY;
		this.z = offsetZ;
		return this;
	}

	public void clear () {
		this.y = 0f;
		this.x = 0f;
		this.z = 0f;
	}
}
