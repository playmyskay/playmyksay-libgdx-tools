package com.playmyskay.voxel.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;

public interface IVoxelTypeProvider {
	public enum Mode {
		COLOR, TEXTURE
	}

	public Mode getMode ();

	public Color getColor (VoxelTypeDescriptor descriptor);

	public Texture getTexture (VoxelTypeDescriptor descriptor);

	public VertexAttributes vertexAttributes ();

	public float[] getExtendedVertices (VoxelTypeDescriptor descriptor);

	public Material getMaterial ();
}
