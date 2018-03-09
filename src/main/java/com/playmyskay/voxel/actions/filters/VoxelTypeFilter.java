package com.playmyskay.voxel.actions.filters;

import java.util.HashSet;
import java.util.Set;

import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.level.VoxelLevelTools;
import com.playmyskay.voxel.type.VoxelType;

public class VoxelTypeFilter extends VoxelLevelFilter {

	private Set<VoxelType> voxelTypeSet = new HashSet<>();

	public VoxelTypeFilter() {

	}

	public VoxelTypeFilter(VoxelType voxelType) {
		add(voxelType);
	}

	public void add (VoxelType voxelType) {
		voxelTypeSet.add(voxelType);
	}

	@Override
	protected boolean filter (VoxelLevel voxelLevel) {
		if (voxelLevel instanceof VoxelLevelEntity) {
			VoxelLevelChunk chunk = VoxelLevelTools.getChunkLevel(voxelLevel);
			if (chunk != null) {
				VoxelComposite voxelComposite = chunk.getVoxelComposite((VoxelLevelEntity) voxelLevel);
				if (voxelComposite != null) {
					for (VoxelType voxelType : voxelTypeSet) {
						if (voxelComposite.voxelTypeDescriptor.voxelType == voxelType) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
