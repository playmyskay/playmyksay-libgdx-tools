package com.playmyskay.voxel.level;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelLevelPool {
//	private VoxelLevelEntityPool entityPool;
//	private VoxelLevelChunkPool chunkPool;
//	private VoxelLevelChunkSpacePool chunkSpacePool;
//	private VoxelLevelChunkSpace1Pool chunkSpace1Pool;
//	private VoxelLevelSpacePool spacePool;

	private ConcurrentLinkedQueue<VoxelLevelEntity> entityPool = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<VoxelLevelChunk> chunkPool = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<VoxelLevelChunkSpace> chunkSpacePool = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<VoxelLevelChunkSpace1> chunkSpace1Pool = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<VoxelLevelSpace> spacePool = new ConcurrentLinkedQueue<>();

	public VoxelLevelPool(VoxelWorld world) {
		int entityPoolSize = (world.cached_chunk_width * world.cached_chunk_depth * world.cached_chunk_height
				* VoxelWorld.CHUNK_DIM) / 2;
		int chunkPoolSize = world.cached_chunk_width * world.cached_chunk_depth * world.cached_chunk_height;
		int chunkSpacePoolSize = (world.cached_chunk_width * world.cached_chunk_depth * world.cached_chunk_height
				* VoxelWorld.CHUNK_DIM) / 3;
		int chunkSpace1PoolSize = (world.cached_chunk_width * world.cached_chunk_depth * world.cached_chunk_height
				* VoxelWorld.CHUNK_DIM) / 4;
		int spacePoolSize = 4096;

		try {
//			initPool(entityPool, entityPoolSize, VoxelLevelEntity.class);
//			initPool(chunkPool, chunkPoolSize, VoxelLevelChunk.class);
//			initPool(chunkSpacePool, chunkSpacePoolSize, VoxelLevelChunkSpace.class);
//			initPool(chunkSpace1Pool, chunkSpace1PoolSize, VoxelLevelChunkSpace1.class);
//			initPool(spacePool, spacePoolSize, VoxelLevelSpace.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		this.entityPool = new VoxelLevelEntityPool(entityPoolSize);
//		this.chunkPool = new VoxelLevelChunkPool(chunkPoolSize);
//		this.chunkSpacePool = new VoxelLevelChunkSpacePool(chunkSpacePoolSize);
//		this.chunkSpace1Pool = new VoxelLevelChunkSpace1Pool();
//		this.spacePool = new VoxelLevelSpacePool(spacePoolSize);
	}

	private static <L extends VoxelLevel> void initPool (ConcurrentLinkedQueue<L> pool, int size, Class<L> clazz)
			throws InstantiationException, IllegalAccessException {
		for (int i = 0; i < size; ++i) {
			pool.add(clazz.newInstance());
		}
	}

	public VoxelLevel obtain (int level) {
		VoxelLevel voxelLevel = null;
		if (level == 0)
			voxelLevel = entityPool.poll();
		else if (level == 1)
			voxelLevel = chunkSpace1Pool.poll();
		else if (level == VoxelWorld.CHUNK_LEVEL)
			voxelLevel = chunkPool.poll();
		else if (level > 0 && level < VoxelWorld.CHUNK_LEVEL)
			voxelLevel = chunkSpacePool.poll();
		else
			voxelLevel = spacePool.poll();

		if (voxelLevel == null) {
			if (level == 0)
				voxelLevel = new VoxelLevelEntity();
			else if (level == 1)
				voxelLevel = new VoxelLevelChunkSpace1();
			else if (level == VoxelWorld.CHUNK_LEVEL)
				voxelLevel = new VoxelLevelChunk();
			else if (level > 0 && level < VoxelWorld.CHUNK_LEVEL)
				voxelLevel = new VoxelLevelChunkSpace();
			else
				voxelLevel = new VoxelLevelSpace();
		}

		return voxelLevel;
	}

	public void free (VoxelLevel level) {
		if (level instanceof VoxelLevelEntity)
			entityPool.offer((VoxelLevelEntity) level);
		else if (level instanceof VoxelLevelChunk)
			chunkPool.offer((VoxelLevelChunk) level);
		else if (level instanceof VoxelLevelChunkSpace1)
			chunkSpace1Pool.offer((VoxelLevelChunkSpace1) level);
		else if (level instanceof VoxelLevelChunkSpace)
			chunkSpacePool.offer((VoxelLevelChunkSpace) level);
		else
			spacePool.offer((VoxelLevelSpace) level);
	}
}
