package com.playmyskay.voxel.level;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.playmyskay.octree.common.OctreeNodeDescriptor;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.type.VoxelTypeDescriptor;

public class VoxelLevelEntity extends VoxelLevel {
	public VoxelTypeDescriptor descriptor;

	@Override
	public BoundingBox boundingBox () {
		throw new GdxRuntimeException("never call this method");
	}

	@Override
	public boolean leaf () {
		return true;
	}

	@Override
	public void update (VoxelLevel node, OctreeNodeDescriptor descriptor) {

	}

	@Override
	public void descriptor (OctreeNodeDescriptor descriptor) {
		if (descriptor instanceof VoxelDescriptor) {
			this.descriptor = ((VoxelDescriptor) descriptor).voxelTypeDescriptor;
		}
		super.descriptor(descriptor);
	}

	@Override
	public VoxelLevel[] childs () {
		throw new GdxRuntimeException("never call this method");
	}

	@Override
	public VoxelLevel[] childs (VoxelLevel[] childs) {
		throw new GdxRuntimeException("never call this method");
	}

}
