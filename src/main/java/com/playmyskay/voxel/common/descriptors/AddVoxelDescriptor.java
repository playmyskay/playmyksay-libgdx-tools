package com.playmyskay.voxel.common.descriptors;

public class AddVoxelDescriptor extends VoxelDescriptor {

	private final static AddVoxelDescriptor addVoxelDescriptor = new AddVoxelDescriptor();

	public static AddVoxelDescriptor getInstance () {
		return addVoxelDescriptor;
	}

	public AddVoxelDescriptor() {
		super(BaseActionType.add);
	}

}
