package com.playmyskay.voxel.face;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

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

	public static int toIndex (int x, int y, int z) {
		return (VoxelWorld.CHUNK_SIZE * VoxelWorld.CHUNK_SIZE) * y + z * (VoxelWorld.CHUNK_SIZE) + x;
	}

	public static VoxelFacePlane handlePlane (VoxelLevelChunk chunk, VoxelLevelEntity[] mask, Direction direction,
			VoxelComposite voxelComposite, List<VoxelFacePlane> planeList, VoxelFacePlane plane, int x, int y, int z) {
		int index = toIndex(x, y, z);
		VoxelLevelEntity entity = mask[index];
		if (entity != null) {
			if (!entity.hasFace(direction)) {
				plane = null;
			} else {
				if (plane == null) {
					plane = createPlane(direction, planeList);

					System.out.println(String.format("create plane: % 3d|% 3d|% 3d  dir: %s", x, y, z, direction));
				}

				plane.add(entity);
			}
		}

		return plane;
	}

	private static void determineVoxelPlanes (VoxelLevelChunk chunk, Direction direction, VoxelComposite voxelComposite,
			List<VoxelFacePlane> planeList) {
		// Es ist eher wahrscheinlich, dass sie im sichtbaren Bereich Richtung
		// links/rechts bzw. in die
		// Tiefe vorkommt
		switch (direction) {
		case left:
		case right:
			IntStream.range(0, VoxelWorld.CHUNK_SIZE).forEach(y -> {
				VoxelFacePlane plane = null;
				VoxelLevelEntity[] mask = createMask(chunk, voxelComposite);
				for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
					for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
						plane = handlePlane(chunk, mask, direction, voxelComposite, planeList, plane, x, y, z);
					}
					plane = null;
				}
			});
			break;
		case top:
		case bottom:
		case front:
		case back:
			IntStream.range(0, VoxelWorld.CHUNK_SIZE).forEach(y -> {
				VoxelFacePlane plane = null;
				VoxelLevelEntity[] mask = createMask(chunk, voxelComposite);
				for (int z = 0; z < VoxelWorld.CHUNK_SIZE; ++z) {
					for (int x = 0; x < VoxelWorld.CHUNK_SIZE; ++x) {
						plane = handlePlane(chunk, mask, direction, voxelComposite, planeList, plane, x, y, z);
					}
					plane = null;
				}
			});
			break;
		default:
			// Assert.isTrue(false);
			break;

		}

		planeList.parallelStream().forEach(plane -> {
			plane.updateDimensions();
		});
	}

	public static int getLevelIndex (int fromLevel, int level, int index) {
		// visualize to level quaders 
		int levelIndex = (int) (index / Math.pow(2, fromLevel - level));
		return levelIndex;
	}

	private static VoxelLevelEntity[] createMask (VoxelLevelChunk chunk, VoxelComposite voxelComposite) {
		VoxelLevelEntity[] mask = new VoxelLevelEntity[VoxelWorld.CHUNK_DIM];
		int chunkLevel = chunk.getDepth();
		VoxelLevel voxelLevel = chunk;
		for (int index = 0; index < VoxelWorld.CHUNK_DIM && voxelLevel != null; ++index) {
			for (int level = chunkLevel - 1; level >= 0 && voxelLevel != null; --level) {
				int levelIndex = getLevelIndex(chunkLevel, level, index);
				voxelLevel = chunk.childs[levelIndex];
			}

			mask[index] = (VoxelLevelEntity) voxelLevel;
		}
		return mask;
	}

	public static void determineVoxelPlaneFaces (VoxelLevelChunk chunk, VoxelComposite voxelComposite) {
		voxelComposite.planeList.clear();
		ReentrantLock lock = new ReentrantLock();
		EnumSet.range(Direction.top, Direction.right).parallelStream().forEach(direction -> {
			System.out.println("build planes dir: " + direction);
			List<VoxelFacePlane> planeList = new ArrayList<VoxelFacePlane>();
			if (direction != Direction.none) {
				determineVoxelPlanes(chunk, direction, voxelComposite, planeList);
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

	public static void determineVoxelPlaneFaces (VoxelLevelChunk chunk, Set<VoxelComposite> voxelCompositeSet) {
		for (VoxelComposite voxelComposite : voxelCompositeSet) {
			determineVoxelPlaneFaces(chunk, voxelComposite);
		}
	}
}
