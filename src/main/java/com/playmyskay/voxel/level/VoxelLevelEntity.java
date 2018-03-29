package com.playmyskay.voxel.level;

import com.playmyskay.octree.common.OctreeNodeDescriptor;
import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.face.VoxelFace;
import com.playmyskay.voxel.face.VoxelFace.Direction;

public class VoxelLevelEntity extends VoxelLevel {

	public byte faceBits = 0x00;
	public byte connectionBits = 0x00;
	public VoxelComposite voxelComposite;

	@Override
	public void update (VoxelLevel node, OctreeNodeDescriptor descriptor) {

	}

	public void addFace (Direction direction) {
		faceBits = VoxelFace.addFace(faceBits, direction);
	}

	public void removeFace (Direction direction) {
		faceBits = VoxelFace.removeFace(faceBits, direction);
	}

	public void addConnection (Direction direction) {
		connectionBits = VoxelFace.addFace(connectionBits, direction);
	}

	public void removeConnection (Direction direction) {
		connectionBits = VoxelFace.removeFace(connectionBits, direction);
	}

	public boolean hasFace (Direction direction) {
		return VoxelFace.hasFace(faceBits, direction);
	}
}
