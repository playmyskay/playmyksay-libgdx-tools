package com.playmyskay.voxel.actions.common;

import com.badlogic.gdx.utils.Disposable;

public abstract class Action implements Disposable {

	public Action() {
	}

	public abstract ActionResult run (ActionData actionData);

}
