package com.playmyskay.voxel.level;

import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelLevelPoolManager {
	private VoxelWorld world;
	private VoxelLevelPool pool;

	public VoxelLevelPoolManager(VoxelWorld world) {
		this.world = world;
		pool = new VoxelLevelPool(world);
	}

	protected VoxelLevelPool newObject () {
		return new VoxelLevelPool(world);
	}

	public VoxelLevel obtain (int level) {
		return pool.obtain(level);
	}

	public void free (VoxelLevel level) {
		pool.free(level);
	}
}
