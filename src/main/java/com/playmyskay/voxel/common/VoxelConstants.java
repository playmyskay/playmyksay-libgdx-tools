package com.playmyskay.voxel.common;

import com.badlogic.gdx.math.Vector3;

public class VoxelConstants {

	public static final Vector3 normal_top = new Vector3(0f, 1f, 0f);
	public static final Vector3 normal_bottom = new Vector3(0f, -1f, 0f);
	public static final Vector3 normal_left = new Vector3(-1f, 0f, 0f);
	public static final Vector3 normal_right = new Vector3(1f, 0f, 0f);
	public static final Vector3 normal_front = new Vector3(0f, 0f, 1f);
	public static final Vector3 normal_back = new Vector3(0f, 0f, -1f);

	public static final Vector3[] normals = new Vector3[] { normal_top, normal_bottom, normal_left, normal_right,
			normal_front, normal_back };
}
