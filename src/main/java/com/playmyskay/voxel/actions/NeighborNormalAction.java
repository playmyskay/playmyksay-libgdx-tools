package com.playmyskay.voxel.actions;

import com.badlogic.gdx.math.Vector3;
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

public class NeighborNormalAction extends Action {
	private VoxelLevelFilter filter;

	public NeighborNormalAction() {
	}

	public NeighborNormalAction(VoxelLevelFilter filter) {
		this.filter = filter;
	}

	@Override
	public ActionResult run (ActionData actionData) {
		actionData.settings().recordLevelSet.clear();
		actionData.settings().recordLevelSet.add(0);
		actionData.settings().filter = filter;

		IntersectionData<VoxelLevel> intersectionData = OctreeTraversal.getIntersectedNormal(actionData.octree(),
				actionData.ray(), actionData.settings());
		if (intersectionData == null) return ActionResult.CONTINUE;
		if (intersectionData.node == null) return ActionResult.CONTINUE;
		if (intersectionData.normal == null) return ActionResult.CONTINUE;

		actionData.pointList()
				.add(intersectionData.node.boundingBox().getCenter(new Vector3()).add(intersectionData.normal));

		actionData.intersectionDataList().add(intersectionData);

		return ActionResult.OK;
	}
}
