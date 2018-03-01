package com.playmyskay.voxel.common;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevel;

public class VoxelWorld {
	public static VoxelWorld voxelWorld = new VoxelWorld();
	public List<VoxelUpdateListener> updateListeners = new ArrayList<VoxelUpdateListener>();

	public VoxelOctree voxelOctree = new VoxelOctree();
	public static int CHUNK_SIZE = 16;
	public static int CHUNK_DIM = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

	private VoxelWorld() {

	}

	public void setVoxel (Vector3 v, VoxelDescriptor descriptor) {
		VoxelLevel voxelLevel = voxelOctree.setNode(v, descriptor);
		updateListeners(voxelLevel);
	}

	public void updateListeners (VoxelLevel voxelLevel) {
		if (voxelLevel != null) {
			for (VoxelUpdateListener updateListener : updateListeners) {
				updateListener.updateLevel(voxelLevel);
			}
		}
	}

	public void addUpdateListener (VoxelUpdateListener updateListener) {
		updateListeners.add(updateListener);
	}
}
