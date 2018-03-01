package com.playmyskay.voxel.actions;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.octree.common.OctreeTraversal;
import com.playmyskay.octree.common.OctreeTraversal.IntersectionData;
import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevel;

/*
 * This action determines the intersected voxel(s) which will be hit by the given ray.
 */

public class NeighborNormalAction extends Action {
	public NeighborNormalAction() {
	}

	public void removeNodes (VoxelDescriptor descriptor) {

	}

	@Override
	public ActionResult run (ActionData actionData) {
		IntersectionData<VoxelLevel> intersectionData = OctreeTraversal.getIntersectedNormal(actionData.octree(),
				actionData.ray());
		if (intersectionData == null) return ActionResult.CONTINUE;
		if (intersectionData.node == null) return ActionResult.CONTINUE;
		if (intersectionData.normal == null) return ActionResult.CONTINUE;

		actionData.pointList()
				.add(intersectionData.node.boundingBox().getCenter(new Vector3()).add(intersectionData.normal));

		actionData.intersectionDataList().add(intersectionData);

		return ActionResult.OK;
	}
}
