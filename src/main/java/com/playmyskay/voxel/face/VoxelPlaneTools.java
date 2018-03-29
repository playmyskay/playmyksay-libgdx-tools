package com.playmyskay.voxel.face;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.log.Logger;
import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.common.VoxelOctree;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelPlaneTools {

	private static boolean mergeHorizontal (List<VoxelFacePlane> planeList) {
		for (VoxelFacePlane plane1 : planeList) {
			for (VoxelFacePlane plane2 : planeList) {
				if (plane1.disposed || plane2.disposed) continue;
				if (plane1 == plane2) continue;
				if (plane1.faceBits != plane2.faceBits) continue;
				if (plane1.y1 == plane2.y1 && plane1.y2 == plane2.y2) {
					if (plane1.x1 == plane2.x1 && plane1.x2 == plane2.x2) {
						if (plane1.z2 == plane2.z1) {
							plane1.z2 = plane2.z2;
							plane2.dispose();
//							voxelComposite.planeList.remove(plane2);
						}
					}
				}
			}
		}

		Iterator<VoxelFacePlane> iterator = planeList.iterator();
		while (iterator.hasNext()) {
			VoxelFacePlane plane = iterator.next();
			if (plane.disposed) {
				iterator.remove();
			}
		}
		return false;
	}

	private static boolean mergeVertical (List<VoxelFacePlane> planeList) {
		for (VoxelFacePlane plane1 : planeList) {
			for (VoxelFacePlane plane2 : planeList) {
				if (plane1.disposed || plane2.disposed) continue;
				if (plane1 == plane2) continue;
				if (plane1.faceBits != plane2.faceBits) continue;
				if (plane1.x1 == plane2.x1 && plane1.x2 == plane2.x2) {
					if (plane1.z1 == plane2.z1 && plane1.z2 == plane2.z2) {
						if (plane1.y2 == plane2.y1) {
							plane1.y2 = plane2.y2;
//							voxelComposite.planeList.remove(plane2);
							plane2.dispose();
						}
					}
				}
			}
		}

		Iterator<VoxelFacePlane> iterator = planeList.iterator();
		while (iterator.hasNext()) {
			VoxelFacePlane plane = iterator.next();
			if (plane.disposed) {
				iterator.remove();
			}
		}
		return false;
	}

	private static void mergePlanes (List<VoxelFacePlane> planeList) {

		while (mergeHorizontal(planeList)) {
		}
		while (mergeVertical(planeList)) {

		}
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
		public VoxelFacePlane plane;
		public VoxelComposite voxelComposite;

		public void reset () {
			plane = null;
			voxelComposite = null;
		}
	}

	public static void handlePlane (PlaneHelper planeHelper, VoxelLevelChunk chunk, VoxelLevelEntity entity,
			Direction direction, int x, int y, int z) {
		if (entity == null || !entity.hasFace(direction)) {
			planeHelper.reset();
			return;
		}

		if (planeHelper.plane == null || planeHelper.voxelComposite != entity.voxelComposite) {
			planeHelper.plane = createPlane(direction);
			planeHelper.voxelComposite = entity.voxelComposite;
			initPlane(planeHelper.plane, direction, x, y, z);

			planeHelper.voxelComposite.planeList.add(planeHelper.plane);
		} else {
			switch (direction) {
			case back:
			case front:
				planeHelper.plane.x2 = x + 1f;
				break;
			case left:
			case right:
				planeHelper.plane.z2 = z + 1f;
				break;
			case bottom:
			case top:
				planeHelper.plane.x2 = x + 1f;
				break;
			default:
				break;
			}
		}
	}

	private static void determineVoxelPlanes (VoxelLevelChunk chunk, VoxelLevelEntity[][][] volume,
			Direction direction) {
		PlaneHelper planeHelper = new PlaneHelper();

		// Es ist eher wahrscheinlich, dass sie im sichtbaren Bereich Richtung
		// links/rechts bzw. in die
		// Tiefe vorkommt
		switch (direction) {
		case left:
		case right:
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
					for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
						handlePlane(planeHelper, chunk, volume[x][y][z], direction, x, y, z);
					}
					planeHelper.reset();
				}
			}
			break;
		case top:
		case bottom:
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
						handlePlane(planeHelper, chunk, volume[x][y][z], direction, x, y, z);
					}
					planeHelper.reset();
				}
			}
			break;
		case front:
		case back:
			for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
				for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
					for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
						handlePlane(planeHelper, chunk, volume[x][y][z], direction, x, y, z);
					}
					planeHelper.reset();
				}
			}
			break;
		default:
			// Assert.isTrue(false);
			break;

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
		int chunkLevel = voxelOctree.nodeProvider.depth(VoxelLevelChunk.class);
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					volume[x][y][z] = (VoxelLevelEntity) getVoxelLevelDown(chunk, chunkLevel, 0, x, y, z);
					if (volume[x][y][z] != null && Logger.get() != null) {
//						Logger.get().log(String.format("entity x(%02d) y(%02d) z(%02d)", x, y, z));
					}
				}
			}
		}
		return volume;
	}

	public static void determineVoxelPlaneFaces (VoxelOctree voxelOctree, VoxelLevelChunk chunk,
			VoxelLevelEntity[][][] volume) {
		EnumSet.range(Direction.top, Direction.right).forEach(direction -> {
//			System.out.println("build planes dir: " + direction);
			if (direction == Direction.bottom) return;
			determineVoxelPlanes(chunk, volume, direction);
		});
	}

	public static void determineVoxelPlaneFaces (VoxelOctree voxelOctree, VoxelLevelChunk chunk) {
		VoxelLevelEntity[][][] volume = createVolume(voxelOctree, chunk);
		determineVoxelPlaneFaces(voxelOctree, chunk, volume);
		chunk.voxelCompositeSet.parallelStream().forEach(voxelComposite -> {
			mergePlanes(voxelComposite.planeList);
			System.out.println("planeCount: " + voxelComposite.planeList.size());
		});
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
