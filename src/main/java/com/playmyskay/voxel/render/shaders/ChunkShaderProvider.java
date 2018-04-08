package com.playmyskay.voxel.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

public class ChunkShaderProvider extends BaseShaderProvider {
	private Config config;

	public ChunkShaderProvider() {
		String vertexShader = Gdx.files.classpath("com/playmyskay/voxel/render/shaders/voxel_vs.glsl").readString();
		String fragmentShader = Gdx.files.classpath("com/playmyskay/voxel/render/shaders/voxel_fs.glsl").readString();

		config = new Config(vertexShader, fragmentShader);
	}

	@Override
	protected Shader createShader (Renderable renderable) {
		return new DefaultShader(renderable, config);
	}

}
