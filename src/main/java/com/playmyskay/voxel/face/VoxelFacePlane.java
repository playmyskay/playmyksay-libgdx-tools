package com.playmyskay.voxel.face;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.playmyskay.voxel.level.VoxelLevelEntity;

public class VoxelFacePlane implements Disposable {
	public byte faceBits = 0x00;

	public float x1 = -1f;
	public float x2 = -1f;
	public float y1 = -1f;
	public float y2 = -1f;
	public float z1 = -1f;
	public float z2 = -1f;

	private List<VoxelLevelEntity> entityList = new ArrayList<VoxelLevelEntity>();

	public void add (VoxelLevelEntity entity) {
		entityList.add(entity);
	}

	public List<VoxelLevelEntity> getEntities () {
		return entityList;
	}

	public float getWidth () {
		return x2 - x1 + 1;
	}

	public float getHeight () {
		return y2 - y1 + 1;
	}

	public float getDepth () {
		return z2 - z1 + 1;
	}

	public void updateDimensions () {
		float x = 0f;
		float y = 0f;
		float z = 0f;

		Vector3 cnt = new Vector3();
		for (VoxelLevelEntity entity : getEntities()) {
			entity.boundingBox.getCenter(cnt);
			x = cnt.x;
			y = cnt.y;
			z = cnt.z;

			if (x1 == -1) x1 = x;
			if (x2 == -1) x2 = x;
			if (y1 == -1) y1 = y;
			if (y2 == -1) y2 = y;
			if (z1 == -1) z1 = z;
			if (z2 == -1) z2 = z;

			if (x1 > x) x1 = x;
			if (x2 < x) x2 = x;
			if (y1 > y) y1 = y;
			if (y2 < y) y2 = y;
			if (z1 > z) z1 = z;
			if (z2 < z) z2 = z;
		}

		x1 = x1 - 0.5f;
		x2 = x2 + 0.5f;
		y1 = y1 - 0.5f;
		y2 = y2 + 0.5f;
		z1 = z1 - 0.5f;
		z2 = z2 + 0.5f;
	}

	@Override
	public void dispose () {

	}
}