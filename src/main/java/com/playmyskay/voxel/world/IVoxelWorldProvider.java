package com.playmyskay.voxel.world;

import com.badlogic.gdx.math.Vector3;

public interface IVoxelWorldProvider {
	public static class WorldData {
		public float x = 0f;
		public float y = 0f;
		public float z = 0f;
		public double h = 0f;
		public double d = 0f;
	}

	public boolean get (WorldData data);

	public boolean get (float x, float y, float z);

	public Vector3 getViewerPostion (Vector3 position);
}
