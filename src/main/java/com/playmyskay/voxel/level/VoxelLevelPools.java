package com.playmyskay.voxel.level;

import com.badlogic.gdx.utils.Pool;

public class VoxelLevelPools {
	public static class VoxelLevelEntityPool extends Pool<VoxelLevelEntity> {
		public VoxelLevelEntityPool(int capacity) {
			super(capacity);
		}

		@Override
		protected VoxelLevelEntity newObject () {
			return new VoxelLevelEntity();
		}
	}

	public static class VoxelLevelChunkPool extends Pool<VoxelLevelChunk> {
		public VoxelLevelChunkPool(int capacity) {
			super(capacity);
		}

		@Override
		protected VoxelLevelChunk newObject () {
			return new VoxelLevelChunk();
		}
	}

	public static class VoxelLevelChunkSpacePool extends Pool<VoxelLevelChunkSpace> {
		public VoxelLevelChunkSpacePool(int capacity) {
			super(capacity);
		}

		@Override
		protected VoxelLevelChunkSpace newObject () {
			return new VoxelLevelChunkSpace();
		}
	}

	public static class VoxelLevelChunkSpace1Pool extends Pool<VoxelLevelChunkSpace1> {
		public VoxelLevelChunkSpace1Pool(int capacity) {
			super(capacity);
		}

		@Override
		protected VoxelLevelChunkSpace1 newObject () {
			return new VoxelLevelChunkSpace1();
		}
	}

	public static class VoxelLevelSpacePool extends Pool<VoxelLevelSpace> {
		public VoxelLevelSpacePool(int capacity) {
			super(capacity);
		}

		@Override
		protected VoxelLevelSpace newObject () {
			return new VoxelLevelSpace();
		}
	}

}
