package com.playmyskay.voxel.world;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.voxel.common.VoxelOctree;
import com.playmyskay.voxel.common.descriptors.AddVoxelDescriptor;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;

public class VoxelWorld {

	public VoxelOctree voxelOctree = new VoxelOctree();
	private IVoxelWorldProvider worldProvider;
	private int width = 16;
	private int height = 16;
	private int depth = 16;
	private static int FEATURE_SIZE = 32;
	public static int CHUNK_SIZE = 16;
	public static int CHUNK_DIM = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

	public static VoxelWorld create (IVoxelWorldProvider worldProvider) {
		return new VoxelWorld(worldProvider);
	}

	private VoxelWorld(IVoxelWorldProvider worldProvider) {
		this.worldProvider = worldProvider;

		createWorld();
	}

	private void createWorld () {
		AddVoxelDescriptor descriptor = new AddVoxelDescriptor();
		descriptor.updateInstant = false;
		descriptor.collectFlag = true;

		Vector3 v = new Vector3();
		for (float x = 0f; x < width; ++x) {
			for (float y = 0f; y < height; ++y) {
				for (float z = 0f; z < depth; ++z) {
					boolean b = worldProvider.get(x / FEATURE_SIZE, y, z / FEATURE_SIZE);
					if (b) {
						v.set(x, y, z);
						setVoxel(v, descriptor);
					}
				}
			}
		}
	}

	public void setVoxel (Vector3 v, VoxelDescriptor descriptor) {
		voxelOctree.setNode(v, descriptor);
	}
}
