package com.playmyskay.voxel.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.type.VoxelTypeDescriptor;

public class VoxelComposite {
	public VoxelTypeDescriptor voxelTypeDescriptor = new VoxelTypeDescriptor();
	public Set<VoxelLevelEntity> voxelLevelSet = new HashSet<VoxelLevelEntity>();
	public List<VoxelFacePlane> planeList = new ArrayList<VoxelFacePlane>();

	public void add (VoxelLevelEntity voxelLevel) {
		voxelLevelSet.add(voxelLevel);
	}

	public void addAll (VoxelComposite voxelComposite) {
		voxelLevelSet.addAll(voxelComposite.voxelLevelSet);
		planeList.addAll(voxelComposite.planeList);
	}

	public int size () {
		return voxelLevelSet.size();
	}

	public boolean contains (VoxelLevel voxelLevel) {
		return voxelLevelSet.contains(voxelLevel);
	}
}
