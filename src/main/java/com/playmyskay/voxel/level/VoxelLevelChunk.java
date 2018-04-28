package com.playmyskay.voxel.level;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.playmyskay.octree.common.OctreeCalc;
import com.playmyskay.octree.common.OctreeCalcPoolManager;
import com.playmyskay.octree.common.OctreeNodeDescriptor;
import com.playmyskay.octree.common.OctreeNodeDescriptor.BaseActionType;
import com.playmyskay.voxel.common.VoxelOctreeProvider;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.face.VoxelFace;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.plane.VoxelPlaneTools;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelLevelChunk extends VoxelLevel {
	public Array<VoxelFacePlane> planeList = new Array<>(true, 16, VoxelFacePlane.class);
	private VoxelLevel[] childs;
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

//	private static VoxelLevelEntity[][] createHeightMap (VoxelLevelChunk chunk) {
//		VoxelLevelEntity[][][] volume = VoxelPlaneTools.createVolume(VoxelOctreeProvider.get(), chunk);
//		VoxelLevelEntity[][] heightMap = new VoxelLevelEntity[VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE];
//
//		final int y_start = VoxelWorld.CHUNK_SIZE / 2;
//		int y_step = 1;
//		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
//			for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
//				if (x == 0 && z == 2) {
//					boolean b = true;
//				}
//				y_step = volume[x][y_start][z] != null ? 1 : -1;
//				for (int y = y_start; y >= 0 && y < VoxelWorld.CHUNK_SIZE; y += y_step) {
//					if (y_step == -1 && volume[x][y][z] != null) {
//						heightMap[x][z] = volume[x][y][z];
//						heightMap[x][z].y = (short) y;
//						break;
//					} else if (y_step == 1) {
//						if (volume[x][y][z] == null) {
//							break;
//						} else {
//							heightMap[x][z] = volume[x][y][z];
//							heightMap[x][z].y = (short) (y);
//						}
//					}
//				}
//			}
//		}
//		return heightMap;
//	}

	@Override
	public void update (VoxelLevel node, OctreeNodeDescriptor descriptor) {
		if (node instanceof VoxelLevelEntity && descriptor instanceof VoxelDescriptor) {
			VoxelDescriptor voxelDescriptor = (VoxelDescriptor) descriptor;
			VoxelLevelEntity voxelLevelEntity = (VoxelLevelEntity) node;
			if (voxelDescriptor.getBaseActionType() == BaseActionType.add) {
				voxelLevelEntity.descriptor = voxelDescriptor.voxelTypeDescriptor;
			} else if (voxelDescriptor.getBaseActionType() == BaseActionType.remove) {
//				VoxelLevelEntity[][] heightMap = createHeightMap(this);
//				VoxelPlaneTools.determineVoxelPlaneFaces(VoxelOctreeProvider.get(), this, heightMap);
			}
		}
	}

	private static void rebuildFaces (VoxelLevelChunk chunk, VoxelLevelEntity[][][] volume) {
		OctreeCalc calc = OctreeCalcPoolManager.obtain();
		calc.octree(VoxelOctreeProvider.get());
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					if (volume[x][y][z] == null) continue;
					determineFaces(chunk, x, y, z, volume, calc);
					calc.reset();

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

		OctreeCalcPoolManager.free(calc);
	}

	public void rebuild (VoxelLevelEntity[][][] volume) {
//		VoxelLevelEntity[][] heightMap = createHeightMap(this);
		rebuildFaces(this, volume);
		VoxelPlaneTools.determineVoxelPlaneFaces(VoxelOctreeProvider.get(), this, volume);
	}

//	private static VoxelLevelEntity getOffsetEntity (VoxelLevelChunk chunk, VoxelLevelEntity entity, int offsetX,
//			int offsetY, int offsetZ, Vector3 v, OctreeCalc calc) {
//		entity.boundingBox(calc).getCenter(v).add(offsetX, offsetY, offsetZ);
//		VoxelLevelEntity offsetEntity = (VoxelLevelEntity) OctreeTraversal.getFromNode(chunk, 4, v, calc);
//		return offsetEntity;
//	}

	private static VoxelLevelEntity getOffsetEntity (VoxelLevelChunk chunk, VoxelLevelEntity[][][] volume, int x, int y,
			int z, int offsetX, int offsetY, int offsetZ) {
		x += offsetX;
		y += offsetY;
		z += offsetZ;
		if (x >= 0 && x < VoxelWorld.CHUNK_SIZE && y >= 0 && y < VoxelWorld.CHUNK_SIZE && z >= 0
				&& z < VoxelWorld.CHUNK_SIZE) {
			return volume[x][y][z];
		}
		return null;
	}

	private static void determineFace (Direction direction, VoxelLevelChunk chunk, VoxelLevelEntity entity, int x,
			int y, int z, VoxelLevelEntity[][][] volume, int offsetX, int offsetY, int offsetZ, OctreeCalc calc) {
		VoxelLevelEntity offsetEntity = getOffsetEntity(chunk, volume, x, y, z, offsetX, offsetY, offsetZ);
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

		calc.reset();
	}

	private static void determineFaces (VoxelLevelChunk chunk, int x, int y, int z, VoxelLevelEntity[][][] volume,
			OctreeCalc calc) {
		VoxelLevelEntity entity = volume[x][y][z];
		entity.faceBits = VoxelFace.getDirectionBit(Direction.none);
		determineFace(Direction.left, chunk, entity, x, y, z, volume, -1, 0, 0, calc);
		determineFace(Direction.right, chunk, entity, x, y, z, volume, 1, 0, 0, calc);
		determineFace(Direction.top, chunk, entity, x, y, z, volume, 0, 1, 0, calc);
		determineFace(Direction.bottom, chunk, entity, x, y, z, volume, 0, -1, 0, calc);
		determineFace(Direction.front, chunk, entity, x, y, z, volume, 0, 0, 1, calc);
		determineFace(Direction.back, chunk, entity, x, y, z, volume, 0, 0, -1, calc);
	}

	@Override
	public VoxelLevel[] childs () {
		return childs;
	}

	@Override
	public VoxelLevel[] childs (VoxelLevel[] childs) {
		this.childs = childs;
		return this.childs;
	}

}
