package com.playmyskay.voxel.world;

import com.badlogic.gdx.math.Vector3;

public interface IVoxelWorldProvider {
	public boolean get (float x, float y, float z);

	public Vector3 getViewerPostion (Vector3 position);
}
