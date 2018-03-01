package com.playmyskay.voxel.render;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.playmyskay.voxel.render.RenderChange.Type;

public class ChunkModelBatch extends ModelBatch {

	private Environment environment;
	private int renderableCount = 0;
	private boolean renderEnabled = true;
	private ConcurrentLinkedQueue<RenderChange> renderQueue = new ConcurrentLinkedQueue<RenderChange>();

	public ConcurrentLinkedQueue<RenderChange> renderQueue () {
		return renderQueue;
	}

	public void environment (Environment environment) {
		this.environment = environment;
	}

	public void renderEnabled (boolean renderEnabled) {
		this.renderEnabled = renderEnabled;
	}

	public int renderableCount () {
		return renderableCount;
	}

	private Renderable createRenderable (RenderableItem item) {
		Renderable renderable = new Renderable();
		renderable.meshPart.set("", null, 0, 0, 0);
		renderable.userData = item;
		renderable.material = item.material;
		renderable.meshPart.mesh = item.mesh;
		renderable.meshPart.offset = 0;
		renderable.meshPart.size = item.mesh.getNumIndices();
		renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
		renderable.environment = environment;
		renderable.shader = shaderProvider.getShader(renderable);
		return renderable;
	}

	public void render () {
		if (!renderEnabled) return;
		while (!renderQueue.isEmpty()) {
			RenderChange rc = renderQueue.poll();
			if (rc.type == Type.create) {
				for (RenderableItem item : rc.renderableItems) {
					item.renderable = createRenderable(item);
					renderables.add(item.renderable);
					renderableCount++;
				}
			} else if (rc.type == Type.remove) {
				for (RenderableItem item : rc.renderableItems) {
					renderables.removeValue(item.renderable, true);
					renderableCount--;
				}
			}
		}
	}

	public void flush () {
		if (!renderEnabled) return;
		sorter.sort(camera, renderables);
		Shader currentShader = null;
		for (int i = 0; i < renderables.size; i++) {
			final Renderable renderable = renderables.get(i);
			if (currentShader != renderable.shader) {
				if (currentShader != null) currentShader.end();
				currentShader = renderable.shader;
				currentShader.begin(camera, context);
			}
			currentShader.render(renderable);
		}
		if (currentShader != null) {
			currentShader.end();
		}
	}

	/**
	 * End rendering one or more {@link Renderable}s. Must be called after a
	 * call to {@link #begin(Camera)}. This will flush the batch, causing any
	 * renderables provided using one of the render() methods to be rendered.
	 * After a call to this method the OpenGL context can be altered again.
	 */
	public void end () {
		flush();
		if (ownsRenderContext()) context.end();
		camera = null;
	}
}