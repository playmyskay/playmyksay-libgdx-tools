package com.playmyskay.voxel.actions;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.common.OctreeCalc;
import com.playmyskay.octree.common.OctreeCalcPoolManager;
import com.playmyskay.octree.traversal.IntersectionData;
import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;
import com.playmyskay.voxel.common.VoxelConstants;
import com.playmyskay.voxel.level.VoxelLevel;

/*
 * This action determines the intersected voxel(s) which are hit by the given ray.
 */

public class NeighborNormalVoxelAction extends Action {
	private OctreeCalc calc = OctreeCalcPoolManager.obtain();

	public static Vector3 calculateNormal (IntersectionData<VoxelLevel> entry, OctreeCalc calc) {
		BoundingBox boundindBox = entry.node.boundingBox(calc);
		Vector3 cnt = boundindBox.getCenter(new Vector3());

		// Build direction vector via difference
		Vector3 direction = new Vector3().set(entry.point).sub(cnt);

		// find the intersected face of the bounding box
		int bestIndex = -1;
		float bestDot = 0f;
		for (int index = 0; index < VoxelConstants.normals.length; ++index) {
			Vector3 normal = VoxelConstants.normals[index];
			float dot = direction.dot(normal);
			if (dot >= 0 && dot > bestDot) {
				bestDot = dot;
				bestIndex = index;
			}
		}

		return VoxelConstants.normals[bestIndex];
	}

	@Override
	public ActionResult run (ActionData actionData) {
		calc.reset();
		calc.octree(actionData.octree());
		actionData.intersectionDataList().forEach(intersectionData -> {
			intersectionData.normal = calculateNormal(intersectionData, calc);
			actionData.pointList()
					.add(intersectionData.node.boundingBox(calc).getCenter(calc.vector()).add(intersectionData.normal));
		});
		return ActionResult.OK;
	}

	@Override
	public void dispose () {
		OctreeCalcPoolManager.free(calc);
	}
}
