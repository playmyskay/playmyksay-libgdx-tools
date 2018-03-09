package com.playmyskay.voxel.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class ChunkModelBatch extends ModelBatch {

	private Environment environment;
	private boolean renderEnabled = true;
	private ConcurrentLinkedQueue<ChunkRenderable> renderQueue = new ConcurrentLinkedQueue<>();
	private HashMap<VoxelLevelChunk, List<Renderable>> map = new HashMap<>();

	public ConcurrentLinkedQueue<ChunkRenderable> renderQueue () {
		return renderQueue;
	}

	public void environment (Environment environment) {
		this.environment = environment;
	}

	public void renderEnabled (boolean renderEnabled) {
		this.renderEnabled = renderEnabled;
	}

	private Renderable createRenderable (RenderableData rd, Mesh mesh) {
		Renderable renderable = new Renderable();
		renderable.meshPart.set("", null, 0, 0, 0);
		renderable.userData = rd.userData();
		renderable.material = rd.material();
		renderable.meshPart.mesh = mesh;
		renderable.meshPart.offset = 0;
		renderable.meshPart.size = mesh.getNumIndices();
		renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
		renderable.environment = environment;
		renderable.shader = shaderProvider.getShader(renderable);
		return renderable;
	}

	public void render () {
		if (!renderEnabled) return;
		while (!renderQueue.isEmpty()) {
			ChunkRenderable cr = renderQueue.poll();
			while (!cr.updateDataQueue.isEmpty()) {
				UpdateData ud = cr.updateDataQueue.poll();
				switch (ud.type) {
				case addChunk:
				case addVoxel: {
					for (RenderableData rd : ud.renderableDatas) {
						Mesh mesh = Mesher.createMesh(rd);
						Renderable renderable = createRenderable(rd, mesh);
						renderables.add(renderable);

						List<Renderable> renderableList = map.get(cr.voxelLevelChunk);
						if (renderableList == null) {
							renderableList = new ArrayList<Renderable>();
							map.put(cr.voxelLevelChunk, renderableList);
						}
						renderableList.add(renderable);
					}
					break;
				}
				case removeChunk: {
					List<Renderable> renderableList = map.get(cr.voxelLevelChunk);
					if (renderableList != null) {
						for (Renderable r : renderableList) {
							renderables.removeValue(r, true);
						}
					}
					break;
				}
				case removeVoxel: {
					List<Renderable> renderableList = map.get(cr.voxelLevelChunk);
					if (renderableList != null) {
						for (Renderable r : renderableList) {
							if (r.userData instanceof VoxelComposite) {
								VoxelComposite voxelComposite = (VoxelComposite) r.userData;
								if (voxelComposite.voxelLevelSet.size() == 0) {
									renderables.removeValue(r, true);
									renderableList.remove(r);
									break;
								}
							}
						}
					}
					break;
				}
				default:
					break;
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