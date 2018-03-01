package com.playmyskay.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

public class CameraController extends InputAdapter {

	private static CameraController cameraController;

	private final Camera camera;
	private final IntIntMap keys = new IntIntMap();
	private int STRAFE_LEFT = Keys.A;
	private int STRAFE_RIGHT = Keys.D;
	private int FORWARD = Keys.W;
	private int BACKWARD = Keys.S;
	private int UP = Keys.Q;
	private int DOWN = Keys.E;
	private float velocity = 5f;
	private float degreesPerPixel = 0.5f;
	private final Vector3 tmp = new Vector3();

	private boolean draggedRotation = true;

	public int TWICE_VELOCITY = Keys.SHIFT_LEFT;

	private CameraController(Camera camera) {
		this.camera = camera;
	}

	public static CameraController createController (Camera camera) {
		if (cameraController == null) {
			cameraController = new CameraController(camera);
		}
		return cameraController;
	}

	public static CameraController get () {
		return cameraController;
	}

	@Override
	public boolean keyDown (int keycode) {
		keys.put(keycode, keycode);
		return true;
	}

	@Override
	public boolean keyUp (int keycode) {
		keys.remove(keycode, 0);
		return true;
	}

	/**
	 * Sets the velocity in units per second for moving forward, backward and
	 * strafing left/right.
	 * 
	 * @param velocity
	 *            the velocity in units per second
	 */
	public void setVelocity (float velocity) {
		this.velocity = velocity;
	}

	/**
	 * Sets how many degrees to rotate per pixel the mouse moved.
	 * 
	 * @param degreesPerPixel
	 */
	public void setDegreesPerPixel (float degreesPerPixel) {
		this.degreesPerPixel = degreesPerPixel;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		if (!draggedRotation) return false;
		float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
		float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
		camera.direction.rotate(camera.up, deltaX);
		tmp.set(camera.direction).crs(camera.up).nor();
		camera.direction.rotate(tmp, deltaY);
// camera.up.rotate(tmp, deltaY);
		return true;
	}

	public void update () {
		update(Gdx.graphics.getDeltaTime());
	}

	public void update (float deltaTime) {
		float velocityTmp = velocity;

		if (keys.containsKey(TWICE_VELOCITY)) {
			velocityTmp *= 2;
		}
		if (keys.containsKey(FORWARD)) {
			tmp.set(camera.direction).nor().scl(deltaTime * velocityTmp);
			camera.position.add(tmp);
		}
		if (keys.containsKey(BACKWARD)) {
			tmp.set(camera.direction).nor().scl(-deltaTime * velocityTmp);
			camera.position.add(tmp);
		}
		if (keys.containsKey(STRAFE_LEFT)) {
			tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocityTmp);
			camera.position.add(tmp);
		}
		if (keys.containsKey(STRAFE_RIGHT)) {
			tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocityTmp);
			camera.position.add(tmp);
		}
		if (keys.containsKey(UP)) {
			tmp.set(camera.up).nor().scl(deltaTime * velocityTmp);
			camera.position.add(tmp);
		}
		if (keys.containsKey(DOWN)) {
			tmp.set(camera.up).nor().scl(-deltaTime * velocityTmp);
			camera.position.add(tmp);
		}
		camera.update(true);
	}

	public void setDraggedRotation (boolean flag) {
		this.draggedRotation = flag;
	}

	public boolean getDraggedRotation () {
		return this.draggedRotation;
	}
}
