package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.FloatArray;
import com.playmyskay.voxel.common.VoxelOffset;
import com.playmyskay.voxel.face.VoxelFace;
import com.playmyskay.voxel.face.VoxelFace.Direction;
import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.type.VoxelTypeDescriptor;

public class VoxelVerticesTools {

	public static Color determineColor (VoxelTypeDescriptor descriptor) {
		switch (descriptor.voxelType) {
		case preview:
			break;
		case selection:
			break;
		case undef:
			break;
		case viewer:
			break;
		case voxel_static:
			break;
		default:
			break;
		}
		//return Color.RED.toFloatBits();
		float min = 0.2f;
		float max = 0.9f;
		return new Color(MathUtils.random(min, max), MathUtils.random(min, max), MathUtils.random(min, max), 0.7f);

//		return new Color(0.8f, 0.5f, 0.8f, 1f);
	}

	final static public int createPlaneVertices (VoxelFacePlane plane, FloatArray vertices, int vertexCount,
			VoxelOffset voxelOffset) {
		Direction direction = VoxelFace.getDirection(plane.faceBits);
		vertexCount = createPlaneVertices(direction, plane, vertices, vertexCount, voxelOffset,
				determineColor(plane.descriptor));
		return vertexCount;
	}

	final static public int createPlaneVertices (Direction direction, VoxelFacePlane plane, FloatArray vertices,
			int vertexCount, VoxelOffset voxelOffset, Color color) {

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
			vertices.add(color.toFloatBits());

			vertexCount += 7;
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
