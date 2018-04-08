package com.playmyskay.voxel.render;

import com.badlogic.gdx.utils.FloatArray;
import com.playmyskay.voxel.common.VoxelOffset;
import com.playmyskay.voxel.face.VoxelFace;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.type.IVoxelTypeProvider.Mode;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelVerticesTools {

	final static public float[] getExtendedData (VoxelWorld world, VoxelFacePlane plane) {
		return world.typeProvider().getExtendedVertices(plane.descriptor);
	}

	final static public int createPlaneVertices (VoxelWorld world, VoxelFacePlane plane, FloatArray vertices,
			int vertexCount, VoxelOffset voxelOffset) {
		Direction direction = VoxelFace.getDirection(plane.faceBits);

		float[] data = getExtendedData(world, plane);
		vertexCount = createPlaneVertices(world, direction, plane, vertices, vertexCount, voxelOffset, data);
		return vertexCount;
	}

	final static public int createPlaneVertices (VoxelWorld world, Direction direction, VoxelFacePlane plane,
			FloatArray vertices, int vertexCount, VoxelOffset voxelOffset, float[] extendedData) {

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
			createNormal(direction, plane, vertices);
			if (extendedData != null) {
				if (world.typeProvider().getMode() == Mode.TEXTURE) {
					if (direction == Direction.top) {
						float data1 = extendedData[0] + 3f;
						float data2 = extendedData[1] + 7f;
						if (addX) data1 = extendedData[2] + 3f + 0.01f * plane.getWidth();
						if (addZ) data2 = extendedData[3] + 7f + 0.01f * plane.getDepth();
						vertices.add(data1);
						vertices.add(data2);
					} else {
						vertices.add(extendedData[0]);
						vertices.add(extendedData[1]);
					}
				} else if (world.typeProvider().getMode() == Mode.COLOR) {
					vertices.addAll(extendedData);
				}
			}
			vertexCount += 8;
		}

		return vertexCount;
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
