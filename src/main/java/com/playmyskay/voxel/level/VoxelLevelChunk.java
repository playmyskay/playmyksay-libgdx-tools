package com.playmyskay.voxel.level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.octree.common.OctreeNodeDescriptor;
import com.playmyskay.octree.common.OctreeNodeDescriptor.BaseActionType;
import com.playmyskay.octree.traversal.OctreeTraversal;
import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.common.VoxelPosition;
import com.playmyskay.voxel.common.VoxelWorld;
import com.playmyskay.voxel.common.descriptors.VoxelDescriptor;
import com.playmyskay.voxel.face.VoxelFace;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.face.VoxelPlaneTools;
import com.playmyskay.voxel.type.VoxelTypeDescriptor;

public class VoxelLevelChunk extends VoxelLevel {
	public Set<VoxelComposite> voxelCompositeSet = new HashSet<VoxelComposite>();

	@Override
	public void update (VoxelLevel node, OctreeNodeDescriptor descriptor) {
		if (node instanceof VoxelLevelEntity && descriptor instanceof VoxelDescriptor) {
			VoxelDescriptor voxelDescriptor = (VoxelDescriptor) descriptor;
			VoxelLevelEntity voxelLevelEntity = (VoxelLevelEntity) node;
			if (voxelDescriptor.getBaseActionType() == BaseActionType.add) {
				VoxelComposite voxelComposite = new VoxelComposite();
				voxelComposite.voxelLevelSet.add(voxelLevelEntity);
				voxelComposite.voxelTypeDescriptor = voxelDescriptor.voxelTypeDescriptor;
				voxelComposite.planeList
						.addAll(Arrays.asList(VoxelPlaneTools.determineVoxelPlaneFacesFast(this, voxelLevelEntity)));

				voxelCompositeSet.add(voxelComposite);
			} else if (voxelDescriptor.getBaseActionType() == BaseActionType.remove) {
				VoxelComposite voxelComposite = getVoxelComposite(voxelLevelEntity);
				voxelComposite.voxelLevelSet.remove(voxelLevelEntity);
				if (voxelComposite.voxelLevelSet.size() == 0) {
					voxelCompositeSet.remove(voxelComposite);
				} else {
					VoxelPlaneTools.determineVoxelPlaneFaces(this, voxelComposite);
				}
			}
		}
	}

	public VoxelComposite getVoxelComposite (VoxelLevelEntity entity) {
		for (VoxelComposite voxelComposite : voxelCompositeSet) {
			for (VoxelLevel item : voxelComposite.voxelLevelSet) {
				if (item == entity) {
					return voxelComposite;
				}
			}
		}
		return null;
	}

	private VoxelLevelEntity getOffsetEntity (VoxelLevelEntity entity, int offsetX, int offsetY, int offsetZ) {
		Vector3 v = entity.boundingBox().getCenter(new Vector3()).add(offsetX, offsetY, offsetZ);
		VoxelLevelEntity offsetEntity = (VoxelLevelEntity) OctreeTraversal.get(VoxelWorld.voxelWorld.voxelOctree, v);
		return offsetEntity;
	}

	private VoxelComposite checkComposite (VoxelLevelEntity entity, VoxelTypeDescriptor voxelTypeDescriptor,
			int offsetX, int offsetY, int offsetZ) {
		VoxelLevelEntity offsetEntity = getOffsetEntity(entity, offsetX, offsetY, offsetZ);
		if (offsetEntity != null) {
			VoxelComposite voxelComposite = getVoxelComposite(offsetEntity);
			if (voxelComposite != null) {
				if (voxelComposite.voxelTypeDescriptor.equal(voxelTypeDescriptor)) {
					return voxelComposite;
				}
			}
		}

		return null;
	}

	VoxelComposite[] voxelComposites = new VoxelComposite[6];
	VoxelPosition tmpVoxelPosition = new VoxelPosition();

	private VoxelComposite mergeToComposite (VoxelLevelEntity entity, VoxelDescriptor descriptor) {
		voxelComposites[0] = checkComposite(entity, descriptor.voxelTypeDescriptor, -1, 0, 0);
		voxelComposites[1] = checkComposite(entity, descriptor.voxelTypeDescriptor, 1, 0, 0);
		voxelComposites[2] = checkComposite(entity, descriptor.voxelTypeDescriptor, 0, -1, 0);
		voxelComposites[3] = checkComposite(entity, descriptor.voxelTypeDescriptor, 0, 1, 0);
		voxelComposites[4] = checkComposite(entity, descriptor.voxelTypeDescriptor, 0, 0, 1);
		voxelComposites[5] = checkComposite(entity, descriptor.voxelTypeDescriptor, 0, 0, -1);

		VoxelComposite mergedVoxelComposite = mergeComposites(entity, descriptor.voxelTypeDescriptor, voxelComposites);
		voxelCompositeSet.add(mergedVoxelComposite);

		return mergedVoxelComposite;
	}

	private VoxelComposite mergeComposites (VoxelLevelEntity entity, VoxelTypeDescriptor voxelTypeDescriptor,
			VoxelComposite[] voxelComposites) {
		VoxelComposite mergedVoxelComposite = null;
		for (VoxelComposite voxelComposite : voxelComposites) {
			if (mergedVoxelComposite == voxelComposite) continue;
			if (voxelComposite != null) {
				if (mergedVoxelComposite == null) {
					mergedVoxelComposite = voxelComposite;
					mergedVoxelComposite.add(entity);
				} else if (mergedVoxelComposite.size() < voxelComposite.size()) {
					voxelCompositeSet.remove(mergedVoxelComposite);
					voxelComposite.addAll(mergedVoxelComposite);
					mergedVoxelComposite = voxelComposite;
				} else {
					voxelCompositeSet.remove(voxelComposite);
					mergedVoxelComposite.addAll(voxelComposite);
				}
			}
		}

		if (mergedVoxelComposite == null) {
			mergedVoxelComposite = new VoxelComposite();
			mergedVoxelComposite.add(entity);
			mergedVoxelComposite.voxelTypeDescriptor = voxelTypeDescriptor.copy();
		}

		return mergedVoxelComposite;
	}

	private void determineFace (VoxelComposite voxelComposite, Direction direction, VoxelLevelEntity entity,
			int offsetX, int offsetY, int offsetZ) {
		VoxelLevelEntity offsetEntity = getOffsetEntity(entity, offsetX, offsetY, offsetZ);
		if (offsetEntity == null) {
			entity.addFace(direction);
		} else {
			if (voxelComposite.contains(offsetEntity)) {
				entity.removeFace(direction);
				offsetEntity.removeFace(VoxelFace.getOpposite(direction));
			} else {
				entity.addFace(direction);
			}
		}

	}

	public void updateAll () {
		for (VoxelComposite voxelComposite : voxelCompositeSet) {
			for (VoxelLevelEntity entity : voxelComposite.voxelLevelSet) {
				determineFaces(voxelComposite, entity);
			}
		}
	}

	private void determineFaces (VoxelComposite voxelComposite, VoxelLevelEntity entity) {
		if (entity != null) {
			entity.faceBits = VoxelFace.getDirectionBit(Direction.none);
			determineFace(voxelComposite, Direction.left, entity, -1, 0, 0);
			determineFace(voxelComposite, Direction.right, entity, 1, 0, 0);
			determineFace(voxelComposite, Direction.top, entity, 0, 1, 0);
			determineFace(voxelComposite, Direction.bottom, entity, 0, -1, 0);
			determineFace(voxelComposite, Direction.front, entity, 0, 0, 1);
			determineFace(voxelComposite, Direction.back, entity, 0, 0, -1);
		}
	}

}
