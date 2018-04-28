package com.playmyskay.voxel.level;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.playmyskay.octree.common.OctreeNodeDescriptor;

public class VoxelLevelChunkSpace1 extends VoxelLevel {
	private VoxelLevel[] childs;
//	private BoundingBox boundingBox = new BoundingBox();
//
//	@Override
//	public boolean hasBoundingBox () {
//		return true;
//	}
//
//	@Override
//	public BoundingBox boundingBox () {
//		return boundingBox;
//	}

	@Override
	public void update (VoxelLevel node, OctreeNodeDescriptor descriptor) {

	}

	@Override
	public VoxelLevel[] childs () {
		return childs;
	}

	@Override
	public VoxelLevel[] childs (VoxelLevel[] childs) {
		this.childs = childs;
		return this.childs;
	}

	@Override
	public BoundingBox boundingBox () {
		throw new GdxRuntimeException("never call this method");
	}

}
