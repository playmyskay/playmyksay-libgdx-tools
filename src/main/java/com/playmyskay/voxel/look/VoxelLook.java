package com.playmyskay.voxel.look;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public abstract class VoxelLook {

	public abstract VoxelLookType getVoxelLookType();

	public Material material = new Material(ColorAttribute.createDiffuse(0f, 1f, 0f, 1f));
}
