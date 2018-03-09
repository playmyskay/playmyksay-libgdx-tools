package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.g3d.Material;
import com.playmyskay.voxel.common.VoxelOffset;

public class RenderableData {
	private Material material;
	private float[] vertices = null;
	private int vertexCount = 0;
	private int indexCount = 0;
	private VoxelOffset voxelOffset;
	private Object userData;

	public Material material () {
		return material;
	}

	public void material (Material material) {
		this.material = material;
	}

	public float[] vertices () {
		if (vertices == null) vertices = new float[Mesher.VERTEX_SIZE_MAX];
		return vertices;
	}

	public VoxelOffset voxelOffset () {
		if (voxelOffset == null) voxelOffset = new VoxelOffset();
		return voxelOffset;
	}

	public int vertexCount () {
		return vertexCount;
	}

	public void vertexCount (int vertexCount) {
		this.vertexCount = vertexCount;
	}

	public void indexCount (int indexCount) {
		this.indexCount = indexCount;
	}

	public int indexCount () {
		return indexCount;
	}

	public Object userData () {
		return userData;
	}

	public void userData (Object userData) {
		this.userData = userData;
	}
}