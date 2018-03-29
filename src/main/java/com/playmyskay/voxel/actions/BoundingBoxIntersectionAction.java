package com.playmyskay.voxel.actions;

import java.util.Arrays;
import java.util.TreeSet;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.traversal.IntersectionRecorder;
import com.playmyskay.octree.traversal.OctreeTraversal;
import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;
import com.playmyskay.voxel.level.VoxelLevel;

public class BoundingBoxIntersectionAction extends Action {
	private BoundingBox boundingBox;
	private TreeSet<Integer> recordLevelSet = new TreeSet<>();
	private int maxLevel = 0;

	public BoundingBoxIntersectionAction(Integer[] recordLevels, BoundingBox boundingBox, int maxLevel) {
		this.recordLevelSet.addAll(Arrays.asList(recordLevels));
		this.boundingBox = boundingBox;
		this.maxLevel = maxLevel;
	}

	@Override
	public ActionResult run (ActionData actionData) {
		actionData.settings().maxLevel = maxLevel;
		actionData.settings().recordLevelSet.clear();
		actionData.settings().recordLevelSet.addAll(recordLevelSet);

		IntersectionRecorder<VoxelLevel> ir = OctreeTraversal.getIntersections(actionData.octree(), boundingBox,
				actionData.settings());
		if (ir != null) {
			actionData.intersectionDataList(true).addAll(ir.intersections);
		}
		return ActionResult.OK;
	}

}
