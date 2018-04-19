package com.playmyskay.voxel.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.playmyskay.voxel.face.VoxelFacePlane;

public interface IVoxelTypeProvider {
	public enum Mode {
		COLOR, TEXTURE
	}

	public Mode getMode ();

	public Color getColor (VoxelTypeDescriptor descriptor);

	public Texture getTexture (VoxelTypeDescriptor descriptor);

	public VertexAttributes vertexAttributes ();

	public float[] getExtendedVertices (VoxelFacePlane plane);

	public Material getMaterial ();
}
