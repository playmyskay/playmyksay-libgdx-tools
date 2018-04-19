package com.playmyskay.voxel.world;

import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.render.UpdateType;

public class UpdateData {
	public UpdateType type;
	public VoxelWorld voxelWorld;
	public VoxelLevelChunk voxelLevelChunk;
	public VoxelLevelEntity voxelLevelEntity;
}