package com.playmyskay.voxel.face;

import com.badlogic.gdx.utils.Disposable;

public class VoxelFacePlane implements Disposable {
	public byte faceBits = 0x00;

	public float x1 = -1f;
	public float x2 = -1f;
	public float y1 = -1f;
	public float y2 = -1f;
	public float z1 = -1f;
	public float z2 = -1f;

	public float getWidth () {
		return Math.abs(x2 - x1);
	}

	public float getHeight () {
		return Math.abs(y2 - y1);
	}

	public float getDepth () {
		return Math.abs(z2 - z1);
	}

	@Override
	public void dispose () {

	}
}