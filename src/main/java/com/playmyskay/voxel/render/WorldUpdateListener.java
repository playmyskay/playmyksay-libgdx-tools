package com.playmyskay.voxel.render;

import com.playmyskay.voxel.common.VoxelOctreeListener;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.level.VoxelLevelTools;

public class WorldUpdateListener extends VoxelOctreeListener {

	private RenderUpdateManager renderableManager;

	public WorldUpdateListener(RenderUpdateManager renderableManager) {
		this.renderableManager = renderableManager;
	}

	@Override
	public void update (NodeUpdateData<VoxelLevel, VoxelDescriptor> nodeUpdateData) {
		VoxelLevel voxelLevel = nodeUpdateData.node;
		if (voxelLevel != null && voxelLevel instanceof VoxelLevelEntity) {
			VoxelLevelChunk voxelLevelChunk = VoxelLevelTools.getChunkLevel(voxelLevel);
			if (voxelLevelChunk != null) {
				UpdateData updateData = new UpdateData();
				switch (nodeUpdateData.descriptor.getBaseActionType()) {
				case add:
					updateData.type = UpdateType.addVoxel;
					break;
				case remove:
					updateData.type = UpdateType.removeVoxel;
					break;
				}
				updateData.voxelLevelChunk = voxelLevelChunk;
				updateData.voxelLevelEntity = (VoxelLevelEntity) voxelLevel;
				renderableManager.add(updateData);
			}
		}
	}
}
