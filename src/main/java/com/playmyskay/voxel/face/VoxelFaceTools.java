package com.playmyskay.voxel.face;

import com.playmyskay.voxel.common.VoxelOffset;
import com.playmyskay.voxel.face.VoxelFace.Direction;

public class VoxelFaceTools {

	final static public int createFaceVertices (VoxelFacePlane plane, float[] vertices, int vertexCount,
			VoxelOffset voxelOffset) {
		for (Direction direction : Direction.values()) {
			if (direction == Direction.none) continue;
			if (VoxelFace.hasFace(plane.faceBits, direction)) {
				vertexCount = createFaceVertices(direction, plane, vertices, vertexCount, voxelOffset);
			}
		}
		return vertexCount;
	}

	final static public int createFaceVertices (Direction direction, VoxelFacePlane plane, float[] vertices,
			int vertexCount, VoxelOffset voxelOffset) {

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

			vertices[vertexCount++] = voxelOffset.x + plane.x1 + (addX ? plane.getWidth() : 0f);
			vertices[vertexCount++] = voxelOffset.y + plane.y1 + (addY ? plane.getHeight() : 0f);
			vertices[vertexCount++] = voxelOffset.z + plane.z1 + (addZ ? plane.getDepth() : 0f);
			vertexCount = createNormal(direction, plane, vertices, vertexCount);
		}

		return vertexCount;
	}

	public final static int createNormal (Direction direction, VoxelFacePlane plane, float[] vertices,
			int vertexCount) {
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

		vertices[vertexCount++] = nx;
		vertices[vertexCount++] = ny;
		vertices[vertexCount++] = nz;

		return vertexCount;
	}

}
