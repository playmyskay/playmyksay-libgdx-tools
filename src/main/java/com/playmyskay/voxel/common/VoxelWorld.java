package com.playmyskay.voxel.common;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;

public class VoxelWorld {
	public static VoxelWorld voxelWorld = new VoxelWorld();

	public VoxelOctree voxelOctree = new VoxelOctree();
	public static int CHUNK_SIZE = 16;
	public static int CHUNK_DIM = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

	private VoxelWorld() {

	}

	public void setVoxel (Vector3 v, VoxelDescriptor descriptor) {
		voxelOctree.setNode(v, descriptor);
	}
}
