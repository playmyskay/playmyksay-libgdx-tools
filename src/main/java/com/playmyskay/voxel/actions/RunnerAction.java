package com.playmyskay.voxel.actions;

import java.util.ArrayList;
import java.util.List;

import com.playmyskay.voxel.actions.common.Action;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.actions.common.ActionResult;

public class RunnerAction extends Action {
	private List<Action> actionList = new ArrayList<>();

	public void clear () {
		actionList.clear();
	}

	public void add (Action action) {
		actionList.add(action);
	}

	@Override
	public ActionResult run (ActionData actionData) {
		boolean continueFlag = false;
		for (Action action : actionList) {
			switch (action.run(actionData)) {
			case OK:
				if (continueFlag) continueFlag = false;
				break;
			case CONTINUE:
				continueFlag = true;
				break;
			case FAILED:
			default:
				return ActionResult.FAILED;
			}
		}

		if (continueFlag) {
			return ActionResult.FAILED;
		}

		return ActionResult.OK;
	}
}
