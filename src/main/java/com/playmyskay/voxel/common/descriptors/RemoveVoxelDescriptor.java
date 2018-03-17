package com.playmyskay.voxel.common.descriptors;

public class RemoveVoxelDescriptor extends VoxelDescriptor {

	private final static RemoveVoxelDescriptor removeVoxelDescriptor = new RemoveVoxelDescriptor();

	public RemoveVoxelDescriptor() {
		super(BaseActionType.remove);
	}

	public static RemoveVoxelDescriptor getInstance () {
		return removeVoxelDescriptor;
	}

}
