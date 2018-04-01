package com.playmyskay.voxel.render;

import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;

public class UpdateData {
	public UpdateType type;
	public VoxelLevelChunk voxelLevelChunk;
	public VoxelLevelEntity voxelLevelEntity;
	public RenderableData renderableData;
}