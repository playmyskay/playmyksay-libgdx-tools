package com.playmyskay.voxel.render;

import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.world.VoxelWorld;

public class UpdateData {
	public UpdateType type;
	public VoxelWorld voxelWorld;
	public VoxelLevelChunk voxelLevelChunk;
	public VoxelLevelEntity voxelLevelEntity;
	public RenderableData renderableData;
}