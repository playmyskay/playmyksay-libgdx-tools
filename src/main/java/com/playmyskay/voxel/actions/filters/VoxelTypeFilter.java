package com.playmyskay.voxel.actions.filters;

import java.util.HashSet;
import java.util.Set;

import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.level.VoxelLevelTools;
import com.playmyskay.voxel.type.VoxelUsageType;

public class VoxelTypeFilter extends VoxelLevelFilter {

	private Set<VoxelUsageType> voxelTypeSet = new HashSet<>();

	public VoxelTypeFilter() {

	}

	public VoxelTypeFilter(VoxelUsageType voxelType) {
		add(voxelType);
	}

	public void add (VoxelUsageType voxelType) {
		voxelTypeSet.add(voxelType);
	}

	@Override
	protected boolean filter (VoxelLevel voxelLevel) {
		if (voxelLevel instanceof VoxelLevelEntity) {
			VoxelLevelChunk chunk = VoxelLevelTools.getChunkLevel(voxelLevel);
			if (chunk != null) {
				for (VoxelUsageType voxelType : voxelTypeSet) {
					if (((VoxelLevelEntity) voxelLevel).descriptor.voxelType == voxelType) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
