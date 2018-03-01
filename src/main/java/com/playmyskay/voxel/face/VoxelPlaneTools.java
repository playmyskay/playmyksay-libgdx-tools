package com.playmyskay.voxel.face;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.playmyskay.log.Logger;
import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.common.VoxelWorld;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.level.VoxelLevelEntity;

public class VoxelPlaneTools {

	private static boolean mergeHorizontal (VoxelComposite voxelComposite) {
		VoxelFacePlane mergePlane = null;
		VoxelFacePlane sourcePlane = null;
		for (VoxelFacePlane plane1 : voxelComposite.planeList) {
			for (VoxelFacePlane plane2 : voxelComposite.planeList) {
				if (plane1 == plane2) continue;
				if (plane1.faceBits != plane2.faceBits) continue;
				if (plane1.y1 == plane2.y1 && plane1.y2 == plane2.y2) {
					if (plane1.x1 == plane2.x1 && plane1.x2 == plane2.x2) {
						if (plane1.z2 + 1 == plane2.z1) {
							mergePlane = plane1.getEntities().size() > plane2.getEntities().size() ? plane1 : plane2;
							sourcePlane = plane1.getEntities().size() > plane2.getEntities().size() ? plane2 : plane1;
							mergePlane.getEntities().addAll(sourcePlane.getEntities());
							voxelComposite.planeList.remove(sourcePlane);
							//printDebug("deleted", sourcePlane);

							sourcePlane.dispose();
							mergePlane.updateDimensions();

							//printDebug("merge h", mergePlane);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static boolean mergeVertical (VoxelComposite voxelComposite) {
		VoxelFacePlane mergePlane = null;
		VoxelFacePlane sourcePlane = null;
		for (VoxelFacePlane plane1 : voxelComposite.planeList) {
			for (VoxelFacePlane plane2 : voxelComposite.planeList) {
				if (plane1 == plane2) continue;
				if (plane1.faceBits != plane2.faceBits) continue;
				if (plane1.x1 == plane2.x1 && plane1.x2 == plane2.x2) {
					if (plane1.z1 == plane2.z1 && plane1.z2 == plane2.z2) {
						if (plane1.y2 + 1 == plane2.y1) {
							mergePlane = plane1.getEntities().size() > plane2.getEntities().size() ? plane1 : plane2;
							sourcePlane = plane1.getEntities().size() > plane2.getEntities().size() ? plane2 : plane1;
							mergePlane.getEntities().addAll(sourcePlane.getEntities());
							voxelComposite.planeList.remove(sourcePlane);
							sourcePlane.dispose();
							mergePlane.updateDimensions();

							//printDebug("merge v", mergePlane);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static void mergePlanes (VoxelComposite voxelComposite) {

		while (mergeHorizontal(voxelComposite)) {
		}
		while (mergeVertical(voxelComposite)) {
		}
	}

	private static VoxelFacePlane createPlane (Direction direction, List<VoxelFacePlane> planeList) {
		VoxelFacePlane plane = new VoxelFacePlane();
		plane.faceBits = VoxelFace.getDirectionBit(direction);
		planeList.add(plane);
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

	public static VoxelFacePlane handlePlane (VoxelLevelChunk chunk, VoxelLevelEntity entity, Direction direction,
			VoxelComposite voxelComposite, List<VoxelFacePlane> planeList, VoxelFacePlane plane) {
		if (entity != null) {
			if (!entity.hasFace(direction)) {
				plane = null;
			} else {
				if (plane == null) {
					plane = createPlane(direction, planeList);
				}

				plane.add(entity);
			}
		}

		return plane;
	}

	private static void determineVoxelPlanes (VoxelLevelChunk chunk, VoxelLevelEntity[][][] mask, Direction direction,
			VoxelComposite voxelComposite, List<VoxelFacePlane> planeList) {
		VoxelFacePlane plane = null;

		// Es ist eher wahrscheinlich, dass sie im sichtbaren Bereich Richtung
		// links/rechts bzw. in die
		// Tiefe vorkommt
		switch (direction) {
		case left:
		case right:
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
					for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
						plane = handlePlane(chunk, mask[x][y][z], direction, voxelComposite, planeList, plane);
					}
					plane = null;
				}
			}
			break;
		case top:
		case bottom:
		case front:
		case back:
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
						plane = handlePlane(chunk, mask[x][y][z], direction, voxelComposite, planeList, plane);
					}
					plane = null;
				}
			}
			break;
		default:
			// Assert.isTrue(false);
			break;

		}

		planeList.parallelStream().forEach(planeItem -> {
			planeItem.updateDimensions();
		});
	}

	private static int getLevelIndex (int fromLevel, int level, int index) {
		// quadrants
		int pow1 = (int) Math.pow(8, fromLevel - level);
		int pow2 = (int) Math.pow(8, level);
		int levelIndex = index / pow1 / pow2;
		if (levelIndex > 0) {
			boolean b = true;
		}
		return levelIndex;
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

		FileWriter fw = null;
		try {
			fw = new FileWriter("/home/playmyskay/temp/20180228/mask.txt");
			int index = 0;
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
							fw.write(String.format(
									"index(%04d)  level(%d)  x(%02d) y(%02d) z(%02d) "
											+ "bx(%02d) by(%02d) bz(%02d)  val(%d) w(%02d) wh(%02d)\n",
									index, level, x, y, z, bx, by, bz, mask[level][x][y][z], w, wh));
						}
						++index;
					}
				}
			}
		} finally {
			if (fw != null) fw.close();
		}
		return mask;
	}

	private static int getChildIndex (int level, int x, int y, int z) {
		if (mask[level][x][y][z] > 7) {
			boolean b = true;
		}
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
			Logger.get().log(String.format("getVoxelLevelDown: entity x(%02d) y(%02d) z(%02d)", x, y, z));
		}
		return voxelLevel;
	}

	private static VoxelLevelEntity[][][] createMask (VoxelLevelChunk chunk, VoxelComposite voxelComposite) {
		VoxelLevelEntity[][][] volume = new VoxelLevelEntity[VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE][VoxelWorld.CHUNK_SIZE];
		int chunkLevel = VoxelWorld.voxelWorld.voxelOctree.nodeProvider.depth(VoxelLevelChunk.class);
		for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
			for (int y = 0; y < VoxelWorld.CHUNK_SIZE; ++y) {
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					volume[x][y][z] = (VoxelLevelEntity) getVoxelLevelDown(chunk, chunkLevel, 0, x, y, z);
					if (volume[x][y][z] != null && Logger.get() != null) {
						Logger.get().log(String.format("entity x(%02d) y(%02d) z(%02d)", x, y, z));
					}
				}
			}
		}
		return volume;
	}

	public static void determineVoxelPlaneFaces (VoxelLevelChunk chunk, VoxelComposite voxelComposite) {
		VoxelLevelEntity[][][] mask = createMask(chunk, voxelComposite);

		voxelComposite.planeList.clear();
		ReentrantLock lock = new ReentrantLock();
		EnumSet.range(Direction.top, Direction.right).parallelStream().forEach(direction -> {
			System.out.println("build planes dir: " + direction);
			List<VoxelFacePlane> planeList = new ArrayList<VoxelFacePlane>();
			if (direction != Direction.none) {
				determineVoxelPlanes(chunk, mask, direction, voxelComposite, planeList);
			}
			if (!planeList.isEmpty()) {
				/*if (CommonGlobal.DEBUG) {
					for (VoxelFacePlane plane : planeList) {
						Assert.notNull(plane);
					}
				}*/
				lock.lock();
				voxelComposite.planeList.addAll(planeList);
				lock.unlock();
			}
		});

		mergePlanes(voxelComposite);
	}

	public static void determineVoxelPlaneFaces (VoxelLevelChunk chunk) {
		for (VoxelComposite voxelComposite : chunk.voxelCompositeSet) {
			determineVoxelPlaneFaces(chunk, voxelComposite);
		}
	}
}
