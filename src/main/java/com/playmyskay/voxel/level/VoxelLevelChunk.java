package com.playmyskay.voxel.level;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.octree.common.OctreeNodeDescriptor;
import com.playmyskay.octree.common.OctreeTraversal;
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
			VoxelComposite voxelComposite = mergeToComposite((VoxelLevelEntity) node, voxelDescriptor);
			if (voxelDescriptor.updateInstant && voxelComposite != null) {
				determineFaces(voxelComposite, (VoxelLevelEntity) node);
				VoxelPlaneTools.determineVoxelPlaneFaces(this, voxelComposite);
			}
		}
	}

	private VoxelComposite getVoxelComposite (VoxelLevelEntity entity) {
		for (VoxelComposite voxelComposite : voxelCompositeSet) {
			for (VoxelLevel item : voxelComposite.voxelLevelSet) {
				if (item == entity) {
					return voxelComposite;
				}
			}
		}
		return null;
	}

	private VoxelComposite checkComposite (VoxelLevelEntity entity, VoxelTypeDescriptor voxelTypeDescriptor,
			int offsetX, int offsetY, int offsetZ) {
		Vector3 v = entity.boundingBox().getCenter(new Vector3()).add(offsetX, offsetY, offsetZ);
		if (!entity.boundingBox().contains(v)) return null;

		VoxelLevelEntity offsetEntity = (VoxelLevelEntity) OctreeTraversal.get(VoxelWorld.voxelWorld.voxelOctree,
				entity, v);

		VoxelComposite voxelComposite = getVoxelComposite(offsetEntity);
		if (voxelComposite != null) {
			if (voxelComposite.voxelTypeDescriptor.equal(voxelTypeDescriptor)) {
				return voxelComposite;
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

		Vector3 v = entity.boundingBox().getCenter(new Vector3()).add(offsetX, offsetY, offsetZ);
		if (!entity.boundingBox().contains(v)) {
			entity.addFace(direction);
			return;
		}

		VoxelLevelEntity offsetEntity = (VoxelLevelEntity) OctreeTraversal.get(VoxelWorld.voxelWorld.voxelOctree,
				entity, v);

		if (offsetEntity != null && voxelComposite.contains(offsetEntity)) {
			entity.removeFace(direction);
			offsetEntity.removeFace(VoxelFace.getOpposite(direction));
		} else {
			entity.addFace(direction);
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
