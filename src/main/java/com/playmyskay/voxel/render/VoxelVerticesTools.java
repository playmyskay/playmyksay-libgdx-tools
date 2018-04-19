package com.playmyskay.voxel.render;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.NumberUtils;
import com.playmyskay.voxel.common.VoxelOffset;
import com.playmyskay.voxel.face.VoxelFace;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.type.IVoxelTypeProvider.Mode;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelVerticesTools {

	final static public float[] getExtendedData (VoxelWorld world, VoxelFacePlane plane) {
		return world.typeProvider().getExtendedVertices(plane);
	}

	final static public void createPlaneVertices (VoxelWorld world, VoxelFacePlane plane, FloatArray vertices,
			VoxelOffset voxelOffset) {
		Direction direction = VoxelFace.getDirection(plane.faceBits);

		float[] data = getExtendedData(world, plane);
		createPlaneVertices(world, direction, plane, vertices, voxelOffset, data);
	}

	final static public float packValues (int r, int g, int b, int a) {
		int packedValue = ((int) (a) << 24) | ((int) (b) << 16) | ((int) (g) << 8) | ((int) (r));
		return NumberUtils.intBitsToFloat(packedValue);
	}

	final static public void createPlaneVertices (VoxelWorld world, Direction direction, VoxelFacePlane plane,
			FloatArray vertices, VoxelOffset voxelOffset, float[] extendedData) {

		boolean addX = false;
		boolean addY = false;
		boolean addZ = false;

		for (int i = 0; i < 4; i++) {
			switch (direction) {
			case left:
				addY = (i == 2 || i == 3);
				addZ = (i == 1 || i == 3);
				break;
			case right:
				addX = true;
				addY = (i == 1 || i == 3);
				addZ = (i == 2 || i == 3);
				break;
			case top:
				addX = (i == 2 || i == 3);
				addY = true;
				addZ = (i == 1 || i == 3);
				break;
			case bottom:
				addX = (i == 1 || i == 3);
				addY = false;
				addZ = (i == 2 || i == 3);
				break;
			case front:
				addX = (i == 1 || i == 3);
				addY = (i == 2 || i == 3);
				addZ = true;
				break;
			case back:
				addX = (i == 2 || i == 3);
				addY = (i == 1 || i == 3);
				break;
			default:
				//Assert.isTrue(false);
				break;
			}
			vertices.add(voxelOffset.x + plane.x1 + (addX ? plane.getWidth() : 0f));
			vertices.add(voxelOffset.y + plane.y1 + (addY ? plane.getHeight() : 0f));
			vertices.add(voxelOffset.z + plane.z1 + (addZ ? plane.getDepth() : 0f));
//			createNormalByType(direction, plane, vertices);
			createNormal(direction, plane, vertices);
			if (extendedData != null) {
				if (world.typeProvider().getMode() == Mode.TEXTURE) {
					int tileOffsetX = (int) extendedData[0];
					int tileOffsetY = (int) extendedData[1];
					if (direction == Direction.top || direction == Direction.bottom) {
						int faceOffsetX = (int) (addX ? plane.getWidth() : 0);
						int faceOffsetY = (int) (addZ ? plane.getDepth() : 0);
						vertices.add(packValues(tileOffsetX, tileOffsetY, faceOffsetX, faceOffsetY));
					} else if (direction == Direction.left || direction == Direction.right) {
						int faceOffsetX = (int) (!addZ ? plane.getDepth() : 0);
						int faceOffsetY = (int) (!addY ? plane.getHeight() : 0);
						vertices.add(packValues(tileOffsetX, tileOffsetY, faceOffsetX, faceOffsetY));
					} else if (direction == Direction.front || direction == Direction.back) {
						int faceOffsetY = (int) (!addY ? plane.getHeight() : 0);
						int faceOffsetX = (int) (!addX ? plane.getWidth() : 0);
						vertices.add(packValues(tileOffsetX, tileOffsetY, faceOffsetX, faceOffsetY));
					}
				} else if (world.typeProvider().getMode() == Mode.COLOR) {
					vertices.addAll(extendedData);
				}
			}
		}
	}

	public final static void createNormalByType (Direction direction, VoxelFacePlane plane, FloatArray vertices) {
		vertices.add(VoxelFace.getDirectionBit(direction));
	}

	public final static void createNormal (Direction direction, VoxelFacePlane plane, FloatArray vertices) {
		float nx = 0f;
		float ny = 0f;
		float nz = 0f;

		switch (direction) {
		case left:
			nx = -1f;
			break;
		case right:
			nx = 1f;
			break;
		case top:
			ny = 1f;
			break;
		case bottom:
			ny = -1f;
			break;
		case front:
			nz = 1f;
			break;
		case back:
			nz = -1f;
			break;
		default:
			//Assert.isTrue(false);
			break;
		}

		vertices.add(nx);
		vertices.add(ny);
		vertices.add(nz);
	}

}
