package com.playmyskay.voxel.actions;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
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

	public NeighborNormalVoxelAction() {
	}

	public static Vector3 calculateNormal (IntersectionData<VoxelLevel> entry) {
		BoundingBox boundindBox = entry.node.boundingBox();
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
		actionData.intersectionDataList().forEach(intersectionData -> {
			intersectionData.normal = calculateNormal(intersectionData);
			actionData.pointList()
					.add(intersectionData.node.boundingBox().getCenter(new Vector3()).add(intersectionData.normal));
		});
		return ActionResult.OK;
	}
}
