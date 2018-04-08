package com.playmyskay.voxel.render.shaders;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class ChunkShader extends DefaultShader {

	public final static Uniform voxelTextureSizeUniform = new Uniform("u_voxelTextureSize");

	public final static Setter voxelTextureSizeSetter = new GlobalSetter() {
		@Override
		public void set (BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, 1f);
		}
	};

	public ChunkShader(Renderable renderable) {
		super(renderable);
		register(voxelTextureSizeUniform, voxelTextureSizeSetter);
	}

	@Override
	public void init () {
		super.init();
	}

	@Override
	public void begin (Camera camera, RenderContext context) {
		super.begin(camera, context);
	}

	@Override
	public void render (Renderable renderable) {
		super.render(renderable);
	}

}
