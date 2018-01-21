package com.playmyskay.voxel.level;

import com.badlogic.gdx.utils.Disposable;
import com.playmyskay.octree.OctreeNode;
import com.playmyskay.voxel.common.VoxelDescriptor;

public abstract class VoxelLevel extends OctreeNode<VoxelLevel, VoxelDescriptor> implements IVoxelLevel, Disposable {

	@Override
	public void dispose () {

	}
}
