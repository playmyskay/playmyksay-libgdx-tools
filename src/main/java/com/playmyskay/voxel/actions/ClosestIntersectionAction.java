package com.playmyskay.voxel.actions;

import com.playmyskay.octree.common.OctreeCalc;
import com.playmyskay.octree.traversal.IntersectionData;
import com.playmyskay.octree.traversal.OctreeTraversal;
import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;
import com.playmyskay.voxel.actions.filters.VoxelLevelFilter;
import com.playmyskay.voxel.level.VoxelLevel;

/*
 * This action determines the intersected voxel(s) which are hit by the given ray.
 */

public class ClosestIntersectionAction extends Action {
	private VoxelLevelFilter filter;
	private OctreeCalc calc = new OctreeCalc();

	public ClosestIntersectionAction() {
	}

	public ClosestIntersectionAction(VoxelLevelFilter filter) {
		this.filter = filter;
	}

	@Override
	public ActionResult run (ActionData actionData) {
		actionData.settings().recordLevelSet.clear();
		actionData.settings().recordLevelSet.add(0);
		actionData.settings().filter = filter;

		IntersectionData<VoxelLevel> intersectionData = OctreeTraversal.getClosestIntersection(actionData.octree(),
				actionData.ray(), actionData.settings(), calc);
		if (intersectionData == null) return ActionResult.CONTINUE;
		if (intersectionData.node == null) return ActionResult.CONTINUE;

		actionData.intersectionDataList().add(intersectionData);

		return ActionResult.OK;
	}
}
