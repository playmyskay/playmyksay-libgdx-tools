package com.playmyskay.voxel.level;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.playmyskay.octree.common.OctreeNodeDescriptor;
import com.playmyskay.octree.common.OctreeNodeDescriptor.BaseActionType;
import com.playmyskay.octree.traversal.OctreeTraversal;
import com.playmyskay.voxel.common.VoxelOctreeProvider;
import com.playmyskay.voxel.common.VoxelPosition;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.face.VoxelFace;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.face.VoxelPlaneTools;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelLevelChunk extends VoxelLevel {
	public Array<VoxelFacePlane> planeList = new Array<>(true, 16, VoxelFacePlane.class);
	private BoundingBox boundingBox = new BoundingBox();
	private boolean valid = false;

	public boolean valid () {
		return valid;
	}

	public void valid (boolean flag) {
		this.valid = flag;
	}

	@Override
	public boolean hasBoundingBox () {
		return true;
	}

	@Override
	public BoundingBox boundingBox () {
		return boundingBox;
	}

	private static VoxelLevelEntity[][] createHeightMap (VoxelLevelChunk chunk) {
		VoxelLevelEntity[][][] volume = VoxelPlaneTools.createVolume(VoxelOctreeProvider.get(), chunk);
		VoxelLevelEntity[][] heightMap = new VoxelLevelEntity[VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE];

		final int y_start = VoxelWorld.CHUNK_SIZE / 2;
		int y_step = 1;
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
				y_step = volume[x][y_start][z] != null ? 1 : -1;
				for (int y = y_start; y >= 0 && y < VoxelWorld.CHUNK_SIZE; y += y_step) {
					if (y_step == -1 && volume[x][y][z] != null) {
						heightMap[x][z] = volume[x][y][z];
						heightMap[x][z].y = (short) y;
						break;
					} else if (y_step == 1) {
						if (volume[x][y][z] == null) {
							break;
						} else {
							heightMap[x][z] = volume[x][y][z];
							heightMap[x][z].y = (short) (y);
						}
					}
				}
			}
		}
		return heightMap;
	}

	@Override
	public void update (VoxelLevel node, OctreeNodeDescriptor descriptor) {
		if (node instanceof VoxelLevelEntity && descriptor instanceof VoxelDescriptor) {
			VoxelDescriptor voxelDescriptor = (VoxelDescriptor) descriptor;
			VoxelLevelEntity voxelLevelEntity = (VoxelLevelEntity) node;
			if (voxelDescriptor.getBaseActionType() == BaseActionType.add) {
				voxelLevelEntity.descriptor = voxelDescriptor.voxelTypeDescriptor;
			} else if (voxelDescriptor.getBaseActionType() == BaseActionType.remove) {
				VoxelLevelEntity[][] heightMap = createHeightMap(this);
				VoxelPlaneTools.determineVoxelPlaneFaces(VoxelOctreeProvider.get(), this, heightMap);
			}
		}
	}

	private static VoxelLevelEntity getOffsetEntity (VoxelLevelEntity entity, int offsetX, int offsetY, int offsetZ) {
		Vector3 v = entity.boundingBox().getCenter(new Vector3()).add(offsetX, offsetY, offsetZ);
		VoxelLevelEntity offsetEntity = (VoxelLevelEntity) OctreeTraversal.get(VoxelOctreeProvider.get(), v);
		return offsetEntity;
	}

	private static void rebuildFaces (VoxelLevelEntity[][] heightMap) {
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
				if (heightMap[x][z] == null) continue;
				determineFaces(heightMap[x][z]);

//				EnumSet.range(Direction.top, Direction.right).forEach(direction -> {
//					if (entity.hasFace(direction)) {
////							Vector3 v = entity.boundingBox().getCorner000(new Vector3());
////								System.out.println(String.format("face x(%2.0f) | y(%2.0f) | z(%2.0f) - face(%s)", v.x, v.y,
////										v.z, direction.toString()));
//					}
//				});
			}
		}

	}

	public void rebuild () {
		VoxelLevelEntity[][] heightMap = createHeightMap(this);
		rebuildFaces(heightMap);
		VoxelPlaneTools.determineVoxelPlaneFaces(VoxelOctreeProvider.get(), this, heightMap);
	}

	VoxelPosition tmpVoxelPosition = new VoxelPosition();

	private static void determineFace (Direction direction, VoxelLevelEntity entity, int offsetX, int offsetY,
			int offsetZ) {
		VoxelLevelEntity offsetEntity = getOffsetEntity(entity, offsetX, offsetY, offsetZ);
		if (offsetEntity == null) {
			entity.addFace(direction);
		} else {
			//if (voxelComposite.contains(offsetEntity)) {
			entity.removeFace(direction);
			offsetEntity.removeFace(VoxelFace.getOpposite(direction));
			//} else {
			//	entity.addFace(direction);
			//}
		}

	}

	private static void determineFaces (VoxelLevelEntity entity) {
		entity.faceBits = VoxelFace.getDirectionBit(Direction.none);
		determineFace(Direction.left, entity, -1, 0, 0);
		determineFace(Direction.right, entity, 1, 0, 0);
		determineFace(Direction.top, entity, 0, 1, 0);
		determineFace(Direction.bottom, entity, 0, -1, 0);
		determineFace(Direction.front, entity, 0, 0, 1);
		determineFace(Direction.back, entity, 0, 0, -1);
	}

}
