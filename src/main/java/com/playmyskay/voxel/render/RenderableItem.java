package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderableItem implements Poolable {

	public Mesh mesh;
	public Material material;
	public Renderable renderable;
	public boolean valid = true;

	@Override
	public void reset () {
		mesh = null;
		material = null;
	}

}
