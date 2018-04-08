package com.playmyskay.voxel.face;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.playmyskay.voxel.common.VoxelOctree;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelPlaneTools {

	private static void removeDisposed (Array<VoxelFacePlane> planeList) {
		Iterator<VoxelFacePlane> iterator = planeList.iterator();
		while (iterator.hasNext()) {
			VoxelFacePlane plane = iterator.next();
			if (plane.isDisposed()) {
				iterator.remove();
			}
		}
		planeList.shrink();
	}

	private static boolean mergeHorizontal (Array<VoxelFacePlane> planeList) {
		boolean ret = false;
		for (VoxelFacePlane plane1 : planeList.items) {
			for (VoxelFacePlane plane2 : planeList.items) {
				if (plane1 == null || plane2 == null) continue;
				if (plane1.isDisposed() || plane2.isDisposed()) continue;
				if (plane1 == plane2) continue;
				if (plane1.faceBits != plane2.faceBits) continue;
				if (plane1.y1 == plane2.y1 && plane1.y2 == plane2.y2) {
					if (plane1.x1 == plane2.x1 && plane1.x2 == plane2.x2) {
						if (plane1.z2 == plane2.z1) {
							plane1.z2 = plane2.z2;
							plane2.dispose();
							ret = true;
						}
					} else if (plane1.z1 == plane2.z1 && plane1.z2 == plane2.z2) {
						if (plane1.x2 == plane2.x1) {
							plane1.x2 = plane2.x2;
							plane2.dispose();
							ret = true;
						}
					}
				}
			}
		}

		removeDisposed(planeList);
		return ret;
	}

	private static boolean mergeVertical (Array<VoxelFacePlane> planeList) {
		boolean ret = false;
		for (VoxelFacePlane plane1 : planeList.items) {
			for (VoxelFacePlane plane2 : planeList.items) {
				if (plane1 == null || plane2 == null) continue;
				if (plane1.isDisposed() || plane2.isDisposed()) continue;
				if (plane1 == plane2) continue;
				if (plane1.faceBits != plane2.faceBits) continue;
				if (plane1.x1 == plane2.x1 && plane1.x2 == plane2.x2) {
					if (plane1.z1 == plane2.z1 && plane1.z2 == plane2.z2) {
						if (plane1.y2 == plane2.y1) {
							plane1.y2 = plane2.y2;
							plane2.dispose();
							ret = true;
						}
					}
				}
			}
		}

		removeDisposed(planeList);
		return ret;
	}

	private static void mergePlanes (Array<VoxelFacePlane> planeList) {
		while (mergeHorizontal(planeList)) {
		}
		while (mergeVertical(planeList)) {
		}

		planeList.shrink();
	}

	private static VoxelFacePlane createPlane (Direction direction) {
		VoxelFacePlane plane = new VoxelFacePlane();
		plane.faceBits = VoxelFace.getDirectionBit(direction);
		return plane;
	}

	private final static int[][][] indexHelper = new int[VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE];

	static {
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					indexHelper[x][y][z] = (VoxelWorld.CHUNK_SIZE * VoxelWorld.CHUNK_SIZE) * y
							+ z * (VoxelWorld.CHUNK_SIZE) + x;
				}
			}
		}
	}

	public static int toIndex (int x, int y, int z) {
		return indexHelper[x][y][z];
	}

	private static void initPlane (VoxelFacePlane plane, Direction direction, int x, int y, int z) {
		switch (direction) {
		case back:
			plane.x1 = x;
			plane.x2 = x + 1f;
			plane.y1 = y;
			plane.y2 = y + 1f;
			plane.z1 = z;
			plane.z2 = z;
			break;
		case front:
			plane.x1 = x;
			plane.x2 = x + 1f;
			plane.y1 = y;
			plane.y2 = y + 1f;
			plane.z1 = z + 1f;
			plane.z2 = z + 1f;
			break;
		case left:
			plane.x1 = x;
			plane.x2 = x;
			plane.y1 = y;
			plane.y2 = y + 1f;
			plane.z1 = z;
			plane.z2 = z + 1;
			break;
		case right:
			plane.x1 = x + 1f;
			plane.x2 = x + 1f;
			plane.y1 = y;
			plane.y2 = y + 1f;
			plane.z1 = z;
			plane.z2 = z + 1;
			break;
		case bottom:
			plane.x1 = x;
			plane.x2 = x + 1f;
			plane.y1 = y;
			plane.y2 = y;
			plane.z1 = z;
			plane.z2 = z + 1f;
			break;
		case top:
			plane.x1 = x;
			plane.x2 = x + 1f;
			plane.y1 = y + 1f;
			plane.y2 = y + 1f;
			plane.z1 = z;
			plane.z2 = z + 1f;
			break;
		default:
			break;

		}
	}

	public static class PlaneHelper {
		public Array<VoxelFacePlane> planeList = new Array<>();
		public VoxelLevelChunk chunk;
		public VoxelFacePlane plane;
		public int y = -1;

		public void reset () {
			plane = null;
		}
	}

//	private static int getHeight (VoxelLevelChunk chunk, VoxelLevelEntity entity) {
//		return entity.y;
//	}

	public static void handlePlane (PlaneHelper planeHelper, VoxelLevelEntity entity, Direction direction, int x, int y,
			int z) {
		if (entity == null || !entity.hasFace(direction)) {
			planeHelper.reset();
			return;
		}

//		int y = getHeight(planeHelper.chunk, entity);
		if (planeHelper.plane == null || planeHelper.plane.descriptor != entity.descriptor || planeHelper.y != y) {
			planeHelper.plane = createPlane(direction);
			planeHelper.plane.descriptor = entity.descriptor;

			initPlane(planeHelper.plane, direction, x, y, z);
			planeHelper.y = y;
			planeHelper.planeList.add(planeHelper.plane);
		} else {
			planeHelper.plane.z2 = z + 1f;
		}
	}

	private static void determineVoxelPlanes (PlaneHelper planeHelper, VoxelLevelEntity[][][] volume,
			Direction direction) {
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					if (x == 0 && z == 2 && direction == Direction.right) {
						boolean b = true;
					}
					handlePlane(planeHelper, volume[x][y][z], direction, x, y, z);
				}
				planeHelper.reset();
			}
		}
	}

	private static int[][][][] mask;

	static {
		try {
			mask = calcMask(5, 0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static int[][][][] calcMask (int originLevel, int targetLevel) throws IOException {
		int dimension = (int) Math.pow(2, originLevel);
		int[][][][] mask = new int[originLevel - targetLevel + 1][dimension][dimension][dimension];

//		FileWriter fw = null;
		try {
//			fw = new FileWriter("/home/playmyskay/temp/20180228/mask.txt");
//			int index = 0;
			int bx = 0;
			int by = 0;
			int bz = 0;
			for (int y = 0; y < dimension; ++y) {
				for (int z = 0; z < dimension; ++z) {
					for (int x = 0; x < dimension; ++x) {
						bx = x;
						by = y;
						bz = z;
						for (int level = originLevel - 1; level > targetLevel; --level) {
							int w = (int) Math.pow(2, level);
							int wh = w / 2;
							bx = x % w;
							by = y % w;
							bz = z % w;
							mask[level][x][y][z] = (bx < wh ? 0 : 1) + (by < wh ? 0 : 4) + (bz < wh ? 0 : 2);
//							fw.write(String.format(
//									"index(%04d)  level(%d)  x(%02d) y(%02d) z(%02d) "
//											+ "bx(%02d) by(%02d) bz(%02d)  val(%d) w(%02d) wh(%02d)\n",
//									index, level, x, y, z, bx, by, bz, mask[level][x][y][z], w, wh));
						}
//						++index;
					}
				}
			}
		} finally {
//			if (fw != null) fw.close();
		}
		return mask;
	}

	private static int getChildIndex (int level, int x, int y, int z) {
		return mask[level][x][y][z];
	}

	private static VoxelLevel getVoxelLevelDown (VoxelLevel voxelLevelOrigin, int originLevel, int targetLevel, int x,
			int y, int z) {
		int childIndex = -1;
		VoxelLevel voxelLevel = voxelLevelOrigin;
		for (int level = originLevel; level > targetLevel && voxelLevel != null; --level) {
			childIndex = getChildIndex(level, x, y, z);
			voxelLevel = voxelLevel.child(childIndex);
		}
		if (voxelLevel != null) {
//			Logger.get().log(String.format("getVoxelLevelDown: entity x(%02d) y(%02d) z(%02d)", x, y, z));
		}
		return voxelLevel;
	}

	public static VoxelLevelEntity[][][] createVolume (VoxelOctree voxelOctree, VoxelLevelChunk chunk) {
		VoxelLevelEntity[][][] volume = new VoxelLevelEntity[VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE];
		int chunkLevel = voxelOctree.nodeProvider.levelIndex(VoxelLevelChunk.class);
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					volume[x][y][z] = (VoxelLevelEntity) getVoxelLevelDown(chunk, chunkLevel, 0, x, y, z);
//					if (Logger.get() != null && x == 0 && z == 2) {
//						Logger.get().log(String.format("entity(%s) x(%02d) y(%02d) z(%02d)",
//								volume[x][y][z] != null ? "1" : "0", x, y, z));
//					}
				}
			}
		}
		return volume;
	}

	private static int getDirectionIndex (Direction direction) {
		switch (direction) {
		case back:
			return 0;
		case front:
			return 1;
		case left:
			return 2;
		case right:
			return 3;
		case top:
			return 4;
		default:
			break;
		}
		throw new RuntimeException();
	}

	private static void determineVoxelPlaneFacesDirection (VoxelOctree voxelOctree, VoxelLevelChunk chunk,
			VoxelLevelEntity[][][] volume) {
		PlaneHelper[] planeHelpers = new PlaneHelper[5];
		EnumSet.range(Direction.top, Direction.right).forEach(direction -> {
//			System.out.println("build planes dir: " + direction);
			if (direction == Direction.bottom) return;

			int directionIndex = getDirectionIndex(direction);
			planeHelpers[directionIndex] = new PlaneHelper();
			planeHelpers[directionIndex].chunk = chunk;
			determineVoxelPlanes(planeHelpers[directionIndex], volume, direction);
		});

		chunk.planeList.clear();
		for (PlaneHelper planeHelper : planeHelpers) {
			if (planeHelper == null) continue;
			chunk.planeList.addAll(planeHelper.planeList);
		}
	}

	public static void determineVoxelPlaneFaces (VoxelOctree voxelOctree, VoxelLevelChunk chunk,
			VoxelLevelEntity[][][] volume) {
		determineVoxelPlaneFacesDirection(voxelOctree, chunk, volume);
		mergePlanes(chunk.planeList);
	}

	public static VoxelFacePlane[] determineVoxelPlaneFacesFast (VoxelLevelChunk voxelLevelChunk,
			VoxelLevelEntity voxelLevelEntity) {
		Vector3 chunkCorner000 = voxelLevelChunk.boundingBox().min;
		Vector3 entityCorner000 = voxelLevelEntity.boundingBox().min;

		Vector3 indexVector = new Vector3().set(entityCorner000).sub(chunkCorner000);

		VoxelFacePlane[] planes = new VoxelFacePlane[6];
		EnumSet.range(Direction.top, Direction.right).forEach(direction -> {
			int i = direction.ordinal();

			planes[i] = new VoxelFacePlane();
			planes[i].faceBits = VoxelFace.addFace(planes[i].faceBits, direction);

			initPlane(planes[i], direction, (int) indexVector.x, (int) indexVector.y, (int) indexVector.z);
		});

		return planes;
	}
}
