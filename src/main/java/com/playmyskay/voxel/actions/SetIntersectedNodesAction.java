package com.playmyskay.voxel.actions;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.octree.traversal.IntersectionData;
import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevel;

public class SetIntersectedNodesAction extends Action {
	private VoxelDescriptor descriptor;

	public SetIntersectedNodesAction(VoxelDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public ActionResult run (ActionData actionData) {
		for (IntersectionData<VoxelLevel> intersectionData : actionData.intersectionDataList()) {
			actionData.nodeList().add(actionData.octree()
					.setNode(intersectionData.node.boundingBox().getCenter(new Vector3()), descriptor));
		}
		return ActionResult.OK;
	}

	@Override
	public void dispose () {

	}

}
