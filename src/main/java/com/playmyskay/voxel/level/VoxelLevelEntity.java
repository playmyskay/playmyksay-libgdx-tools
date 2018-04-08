package com.playmyskay.voxel.level;

import com.playmyskay.octree.common.OctreeNodeDescriptor;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.face.VoxelFace;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.type.VoxelTypeDescriptor;

public class VoxelLevelEntity extends VoxelLevel {
//	public short y = 0;
	public byte faceBits = 0x00;
	public VoxelTypeDescriptor descriptor;
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

	public void addFace (Direction direction) {
		faceBits = VoxelFace.addFace(faceBits, direction);
	}

	public void removeFace (Direction direction) {
		faceBits = VoxelFace.removeFace(faceBits, direction);
	}

	public boolean hasFace (Direction direction) {
		return VoxelFace.hasFace(faceBits, direction);
	}

	@Override
	public void descriptor (OctreeNodeDescriptor descriptor) {
		if (descriptor instanceof VoxelDescriptor) {
			this.descriptor = ((VoxelDescriptor) descriptor).voxelTypeDescriptor;
		}
		super.descriptor(descriptor);
	}

}
