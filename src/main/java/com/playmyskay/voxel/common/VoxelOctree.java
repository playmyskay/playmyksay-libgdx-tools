package com.playmyskay.voxel.common;

import com.playmyskay.octree.IOctreeNodeProvider;
import com.playmyskay.octree.Octree;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.level.VoxelLevelSpace;

public class VoxelOctree extends Octree<VoxelLevel, VoxelDescriptor> {

	public static class VoxelOctreeNodeProvider implements IOctreeNodeProvider<VoxelLevel> {

		@Override
		public VoxelLevel create (int level) {
			switch (level) {
			case 0:
				return new VoxelLevelEntity();
			case 4:
				return new VoxelLevelChunk();
			default:
				return new VoxelLevelSpace();
			}
		}

		@Override
		public VoxelLevel[] createArray (int level, int size) {
			return new VoxelLevel[size];
		}
	}

	public VoxelOctree() {
		super(new VoxelOctreeNodeProvider());
	}

}
