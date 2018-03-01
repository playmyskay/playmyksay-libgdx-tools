package com.playmyskay.voxel.actions;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.octree.common.OctreeNodeDescriptor.BaseActionType;
import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevel;

public class RemoveNodesAction extends Action {
	private VoxelDescriptor descriptor;

	public RemoveNodesAction() {
		this.descriptor = new VoxelDescriptor(BaseActionType.remove);
	}

	@Override
	public ActionResult run (ActionData actionData) {
		Vector3 cnt = new Vector3();
		for (VoxelLevel node : actionData.nodeList()) {
			actionData.octree().setNode(node.boundingBox().getCenter(cnt), descriptor);
		}
		return ActionResult.OK;
	}

}
