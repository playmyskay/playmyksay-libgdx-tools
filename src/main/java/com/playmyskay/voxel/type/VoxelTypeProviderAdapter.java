package com.playmyskay.voxel.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.playmyskay.voxel.face.VoxelFacePlane;

public abstract class VoxelTypeProviderAdapter implements IVoxelTypeProvider {

	@Override
	public VertexAttributes vertexAttributes () {
		return null;
	}

	@Override
	public float[] getExtendedVertices (VoxelFacePlane plane) {
		return null;
	}

	@Override
	public Color getColor (VoxelTypeDescriptor descriptor) {
		return null;
	}

	@Override
	public Texture getTexture (VoxelTypeDescriptor descriptor) {
		return null;
	}

}
