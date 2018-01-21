package com.playmyskay.voxel.common;

public class VoxelWorld {
	public static VoxelWorld voxelWorld = new VoxelWorld();

	public VoxelOctree voxelOctree = new VoxelOctree();
	public static int CHUNK_SIZE = 16;
	public static int CHUNK_DIM = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

	private VoxelWorld() {

	}
}
