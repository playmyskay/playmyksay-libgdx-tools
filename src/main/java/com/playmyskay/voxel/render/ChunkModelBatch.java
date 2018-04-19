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
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.utils.Pool;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.render.shaders.ChunkShaderProvider;
import com.playmyskay.voxel.world.VoxelWorld;

public class ChunkModelBatch extends ModelBatch implements RenderableProvider {

	protected static class RenderablePool extends FlushablePool<Renderable> {
		public RenderablePool() {
			super(1024);
		}

		@Override
		protected Renderable newObject () {
			return new Renderable();
		}

		@Override
		public Renderable obtain () {
			Renderable renderable = super.obtain();
			renderable.environment = null;
			renderable.material = null;
			renderable.meshPart.set("", null, 0, 0, 0);
			renderable.shader = null;
			renderable.userData = null;
			return renderable;
		}
	}

	private class MeshPool implements Disposable {
		private Array<Mesh> freeMeshes = new Array<Mesh>();
		private Array<Mesh> usedMeshes = new Array<Mesh>();
		public int vertexSizeDelta = 1024;

		public void free (Mesh mesh) {
			freeMeshes.add(mesh);
			usedMeshes.removeValue(mesh, true);
		}

		public Mesh obtain (VoxelWorld world, int vertexCount) {
			int maxVertexCount = vertexSizeDelta;
			while (maxVertexCount < vertexCount) {
				maxVertexCount += vertexSizeDelta;
			}

			int maxIndexCount = (maxVertexCount / 4) * 6;

			for (int i = 0, n = freeMeshes.size; i < n; ++i) {
				final Mesh mesh = freeMeshes.get(i);
				int maxVertices = mesh.getMaxVertices();
				if (maxVertices == maxVertexCount) {
					freeMeshes.removeIndex(i);
					usedMeshes.add(mesh);
					return mesh;
				}
			}
			Mesh result = new Mesh(true, maxVertexCount, maxIndexCount, world.typeProvider.vertexAttributes());
			usedMeshes.add(result);
			return result;
		}

		@Override
		public void dispose () {
			for (Mesh m : usedMeshes)
				m.dispose();
			usedMeshes.clear();
			for (Mesh m : freeMeshes)
				m.dispose();
			freeMeshes.clear();
		}
	}

	private Environment environment;
	private boolean renderEnabled = true;
	private ConcurrentLinkedQueue<RenderUpdateData> addQueue = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<RenderUpdateData> removeQueue = new ConcurrentLinkedQueue<>();
//	private LinkedList<RenderUpdateData> renderQueue = new LinkedList<>();
	private HashMap<VoxelLevelChunk, List<Renderable>> chunkMap = new HashMap<>();
	private int vertexCount = 0;
//	public ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private RenderablePool renderablePool = new RenderablePool();
	private ModelCache modelCache = new ModelCache();
	private boolean useModelCache = false;
	private MeshPool meshPool = new MeshPool();

	public ChunkModelBatch() {
		super(new ChunkShaderProvider());
	}

	public boolean addUpdateData (RenderUpdateData updateData) {
		switch (updateData.type) {
		case addChunk:
		case addVoxel:
			addQueue.add(updateData);
			break;
		case removeChunk:
		case removeVoxel:
			removeQueue.add(updateData);
			break;
		}
		return true;
	}

	public void environment (Environment environment) {
		this.environment = environment;
	}

	public void renderEnabled (boolean renderEnabled) {
		this.renderEnabled = renderEnabled;
	}

	private Renderable createRenderable (RenderableData rd, Mesh mesh) {
		Renderable renderable = renderablePool.obtain();
		renderable.meshPart.set("", null, 0, 0, 0);
		renderable.userData = rd.userData();
		renderable.material = rd.material();
		renderable.meshPart.mesh = mesh;
		renderable.meshPart.offset = 0;
		renderable.meshPart.size = mesh.getNumIndices();
		renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
		renderable.environment = environment;
		renderable.shader = shaderProvider.getShader(renderable);

//		vertexCount += mesh.getNumVertices();

		return renderable;
	}

	private void add (Renderable renderable) {
		renderables.add(renderable);
		vertexCount += renderable.meshPart.mesh.getNumVertices();
	}

	private void remove (Renderable renderable) {
		renderables.removeValue(renderable, true);
		vertexCount -= renderable.meshPart.mesh.getNumVertices();

		meshPool.free(renderable.meshPart.mesh);
		renderablePool.free(renderable);
	}

	private void removeChunk (VoxelLevelChunk chunk) {
		List<Renderable> renderableList = chunkMap.get(chunk);
		if (renderableList != null) {
			for (Renderable renderable : renderableList) {
				remove(renderable);
			}
			chunkMap.remove(chunk);
		}
	}

	private void removeVoxel (VoxelLevelChunk chunk) {
		List<Renderable> renderableList = chunkMap.get(chunk);
		if (renderableList != null) {
			for (Renderable renderable : renderableList) {
//				if (renderable.userData instanceof VoxelComposite) {
//					VoxelComposite voxelComposite = (VoxelComposite) renderable.userData;
//					if (voxelComposite.voxelLevelSet.size() == 0) {
//						remove(renderable);
//						break;
//					}
//				}
			}
		}
	}

	private void add (VoxelWorld world, RenderableData rd, VoxelLevelChunk chunk) {
//		Mesh mesh = ChunkMesher.createMesh(world, rd);
		Mesh mesh = meshPool.obtain(world, rd.vertexCount());
		ChunkMesher.setMeshdata(mesh, rd);
		if (mesh == null) return;

		Renderable renderable = createRenderable(rd, mesh);
		add(renderable);

		List<Renderable> renderableList = chunkMap.get(chunk);
		if (renderableList == null) {
			renderableList = new ArrayList<Renderable>();
			chunkMap.put(chunk, renderableList);
		}
		renderableList.add(renderable);
	}

	@Override
	public void begin (Camera cam) {
		super.begin(cam);
	}

	public final static long MAX_UPDATE_TIME_NS = 500000;
	private int updateCount = 0;
	private long time_start = 0;
	private long time_delta = 0;
	private long time_highest = 0;

	private void processQueue (ConcurrentLinkedQueue<RenderUpdateData> queue) {
		while (/*time_delta < MAX_UPDATE_TIME_NS && */ !queue.isEmpty()) {
			RenderUpdateData ud = queue.poll();
			switch (ud.type) {
			case addChunk:
			case addVoxel:
				add(ud.voxelWorld, ud.renderableData, ud.voxelLevelChunk);
				break;
			case removeChunk:
				removeChunk(ud.voxelLevelChunk);
				break;
			case removeVoxel:
				removeVoxel(ud.voxelLevelChunk);
				break;
			default:
				break;
			}
			++updateCount;
			time_delta = System.nanoTime() - time_start;
		}
	}

	public void render () {
		if (!renderEnabled) return;

		updateCount = 0;
		time_delta = 0;
		time_start = System.nanoTime();

		boolean updateModelCache = useModelCache && (!addQueue.isEmpty() || !removeQueue.isEmpty());
		if (updateModelCache) {
			modelCache.begin(getCamera());
		}

		processQueue(removeQueue);
		processQueue(addQueue);

		if (time_highest > 800000) {
			time_highest = 0;
		}

		if ((System.nanoTime() - time_start) > time_highest) {
			time_highest = System.nanoTime() - time_start;
		}

//		lock.writeLock().unlock();

//		System.out.println(String.format("render updates processed: %d / %d ms (highest: %d)", updateCount,
//				(System.nanoTime() - time_start) / 1000, time_highest / 1000));

		if (updateModelCache) {
			modelCache.add(this);
			modelCache.end();
		}
	}

	private Array<Renderable> renderablesArray = new Array<>();

	public void flush () {
		if (!renderEnabled) return;

		if (useModelCache) {
			if (updateCount > 0) {
				renderablesArray.clear();
				modelCache.getRenderables(renderablesArray, null);
			}
		} else {
			if (renderablesArray != renderables) renderablesArray = renderables;
			sorter.sort(camera, renderables);
		}

		Shader currentShader = null;
		for (int i = 0; i < renderablesArray.size; i++) {
			final Renderable renderable = renderablesArray.get(i);
			if (currentShader == null || currentShader != renderable.shader) {
				if (currentShader != null) currentShader.end();
//				currentShader = renderable.shader;
				currentShader = shaderProvider.getShader(renderable);
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

	public int renderableCount () {
		if (useModelCache) return renderablesArray.size;
		return renderables.size;
	}

	public int chunkCount () {
		return chunkMap.size();
	}

	public int vertexCount () {
		return vertexCount;
	}

	public int updateQueueSize () {
		return addQueue.size();
	}

	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		renderables.addAll(this.renderables);
	}
}