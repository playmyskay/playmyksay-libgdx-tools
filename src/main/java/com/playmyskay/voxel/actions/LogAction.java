package com.playmyskay.voxel.actions;

import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.log.ILogger;
import com.playmyskay.octree.traversal.IntersectionData;
import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;
import com.playmyskay.voxel.level.VoxelLevel;

public class LogAction extends Action {

	public enum LogIntersectionType {
		all, last, nodes
	}

	private LogIntersectionType logIntersectionType;

	public LogAction(LogIntersectionType logIntersectionType) {
		this.logIntersectionType = logIntersectionType;
	}

	private void log (ActionData actionData) {
		if (logIntersectionType == null) return;
		if (actionData.intersectionDataList(false) == null) return;
		if (actionData.intersectionDataList().size() == 0) return;

		switch (logIntersectionType) {
		case all:
			logList(actionData, actionData.intersectionDataList());
			break;
		case last:
			log(actionData, actionData.intersectionDataList().get(actionData.intersectionDataList().size() - 1));
			break;
		case nodes:
			log(actionData, actionData.nodeList());
			break;
		default:
			throw new RuntimeException("Invalid intersection type: " + logIntersectionType.toString());
		}
	}

	private void log (ActionData actionData, List<VoxelLevel> nodeList) {
		ILogger logger = actionData.logger();
		if (logger == null) return;
		if (nodeList == null) return;
		if (nodeList.isEmpty()) return;

		Vector3 cnt = new Vector3();
		for (VoxelLevel voxelLevel : nodeList) {
			voxelLevel.boundingBox().getCenter(cnt);
			logger.log(String.format("%s: (%f, %f, %f)", actionData.identifier(), cnt.x, cnt.y, cnt.z));
		}
	}

	private void log (ActionData actionData, IntersectionData<VoxelLevel> id) {
		ILogger logger = actionData.logger();
		if (logger == null) return;
		if (id == null) return;
		if (id.point != null) {
			logger.log("node: " + String.format("(%f, %f, %f)", id.point.x, id.point.y, id.point.z));
		}
	}

	private void logList (ActionData actionData, List<IntersectionData<VoxelLevel>> ids) {
		for (IntersectionData<VoxelLevel> id : ids) {
			log(actionData, id);
		}
	}

	@Override
	public ActionResult run (ActionData actionData) {
		log(actionData);
		return ActionResult.OK;
	}

	@Override
	public void dispose () {

	}

}
