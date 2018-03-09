package com.playmyskay.voxel.level;

public class VoxelLevelTools {
	public static VoxelLevelChunk getChunkLevel (VoxelLevel voxelLevel) {
		while (voxelLevel != null && !(voxelLevel instanceof VoxelLevelChunk)) {
			voxelLevel = voxelLevel.parent();
		}
		return (VoxelLevelChunk) voxelLevel;
	}
}
