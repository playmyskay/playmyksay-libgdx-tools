package com.playmyskay.voxel.actions;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;

public class SetPointNodesAction extends Action {
	private VoxelDescriptor descriptor;

	public SetPointNodesAction(VoxelDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public ActionResult run (ActionData actionData) {
		for (Vector3 point : actionData.pointList()) {
			actionData.octree().setNode(point, descriptor);
		}
		return ActionResult.OK;
	}

}
