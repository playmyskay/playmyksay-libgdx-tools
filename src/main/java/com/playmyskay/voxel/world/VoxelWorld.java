package com.playmyskay.voxel.world;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.common.OctreeNodeTools;
import com.playmyskay.voxel.common.VoxelOctree;
import com.playmyskay.voxel.common.VoxelOctreeProvider;
import com.playmyskay.voxel.common.descriptors.AddVoxelDescriptor;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.type.IVoxelTypeProvider;

public class VoxelWorld {

	public VoxelOctree voxelOctree = new VoxelOctree();
	private IVoxelWorldProvider worldProvider;
	private IVoxelTypeProvider typeProvider;
	private int chunk_width = 4;
	private int chunk_height = 4;
	private int chunk_depth = 4;
	public static int CHUNK_SIZE = 16;
	public static int CHUNK_DIM = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

	public static VoxelWorld create (IVoxelWorldProvider worldProvider, IVoxelTypeProvider typeProvider) {
		return new VoxelWorld(worldProvider, typeProvider);
	}

	private VoxelWorld(IVoxelWorldProvider worldProvider, IVoxelTypeProvider typeProvider) {
		this.worldProvider = worldProvider;
		this.typeProvider = typeProvider;

		VoxelOctreeProvider.set(voxelOctree);

		Runnable r = new Runnable() {
			@Override
			public void run () {
				createWorld();
			}
		};
		new Thread(r).start();
	}

	private BoundingBox tmpBoundingBox = new BoundingBox();

	private void createChunk (Vector3 v, int chunk_pos_x, int chunk_pos_y, int chunk_pos_z,
			AddVoxelDescriptor descriptor) {
		int offset_x = chunk_pos_x * VoxelWorld.CHUNK_SIZE;
		int offset_y = chunk_pos_y * VoxelWorld.CHUNK_SIZE;
		int offset_z = chunk_pos_z * VoxelWorld.CHUNK_SIZE;
		float cur_pos_x = 0f;
		float cur_pos_y = 0f;
		float cur_pos_z = 0f;

		VoxelLevelChunk chunk = new VoxelLevelChunk();
		chunk.boundingBox().set(new Vector3(offset_x, offset_y, offset_z), new Vector3(offset_x + VoxelWorld.CHUNK_SIZE,
				offset_y + VoxelWorld.CHUNK_SIZE, offset_z + VoxelWorld.CHUNK_SIZE));

		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					cur_pos_x = (offset_x + x);
					cur_pos_y = (offset_y + y);
					cur_pos_z = (offset_z + z);
					if (worldProvider.get(cur_pos_x, cur_pos_y, cur_pos_z)) {
						v.set(offset_x + x, offset_y + y, offset_z + z);
//						if (Logger.get() != null) {
////							Logger.get().log(String.format("x%.0f y%.0f z%.0f", v.x, v.y, v.z));
//						}
						OctreeNodeTools.addNodeByVector(voxelOctree.nodeProvider, chunk, v, descriptor, tmpBoundingBox);
					}
				}
			}
		}

		voxelOctree.addNode(chunk, descriptor);
		chunk.valid(true);
	}

	private void createWorld () {
		AddVoxelDescriptor descriptor = new AddVoxelDescriptor();
		descriptor.updateInstant = false;

		Vector3 v = new Vector3();
		int chunk_count = 0;
		for (int x = 0; x < chunk_width; ++x) {
			for (int y = 0; y < chunk_height; ++y) {
				for (int z = 0; z < chunk_depth; ++z) {
					createChunk(v, x, y, z, descriptor);
//					if (chunk_count % 2 == 0) {
//						System.out.println("chunks " + chunk_count);
//					}
					chunk_count++;
				}
			}
		}
	}

	public void setVoxel (Vector3 v, VoxelDescriptor descriptor) {
		voxelOctree.setNode(v, descriptor);
	}

	public IVoxelTypeProvider typeProvider () {
		return typeProvider;
	}
}
