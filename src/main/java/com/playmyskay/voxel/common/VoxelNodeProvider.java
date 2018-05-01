package com.playmyskay.voxel.common;

import com.playmyskay.octree.common.IOctreeNodeProvider;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.level.VoxelLevelPoolManager;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelNodeProvider implements IOctreeNodeProvider<VoxelLevel> {
	private VoxelLevelPoolManager poolManager;

	public VoxelNodeProvider(VoxelWorld world) {
		poolManager = new VoxelLevelPoolManager(world);
	}

	@Override
	public VoxelLevel create (int level) {
		return poolManager.obtain(level);
	}

	@Override
	public VoxelLevel[] createArray (int level, int size) {
		return new VoxelLevel[size];
	}

	@Override
	public int levelIndex (Class<?> clazz) {
		if (clazz.equals(VoxelLevelEntity.class)) return 0;
		if (clazz.equals(VoxelLevelChunk.class)) return VoxelWorld.CHUNK_LEVEL;
		return -1;
	}

	public void free (VoxelLevel node) {
//		if (!node.leaf() && node.childs() != null) {
//			for (VoxelLevel child : node.childs()) {
//				if (child == null) continue;
//				free(child);
//			}
//			node.childs(null);
//		}
		poolManager.free(node);
	}

}