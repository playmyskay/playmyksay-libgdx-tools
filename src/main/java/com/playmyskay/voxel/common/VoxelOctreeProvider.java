package com.playmyskay.voxel.common;

public class VoxelOctreeProvider {
	private static VoxelOctreeProvider provider = new VoxelOctreeProvider();
	private VoxelOctree voxelOctree;

	public static void set (VoxelOctree voxelOctree) {
		provider.voxelOctree = voxelOctree;
	}

	public static VoxelOctree get () {
		return provider.voxelOctree;
	}
}
