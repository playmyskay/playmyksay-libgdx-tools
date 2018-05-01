package com.playmyskay.voxel.level;

import com.badlogic.gdx.utils.Disposable;
import com.playmyskay.octree.common.OctreeNode;

public abstract class VoxelLevel extends OctreeNode<VoxelLevel> implements IVoxelLevel, Disposable {

	@Override
	public boolean leaf () {
		return false;
	}

	@Override
	public void dispose () {

	}
}
