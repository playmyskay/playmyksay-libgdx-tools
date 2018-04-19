package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.playmyskay.voxel.world.VoxelWorld;

public class VoxelWorldRenderer {
	private ChunkModelBatch chunkModelBatch = new ChunkModelBatch();
	public RenderUpdateManager renderUpdateManager = new RenderUpdateManager(new RenderableUpdater(chunkModelBatch));
	private Camera camera;
	private VoxelWorld voxelWorld;

	public VoxelWorldRenderer(VoxelWorld voxelWorld) {
		this.voxelWorld = voxelWorld;
		voxelWorld.chunkManager.addUpdateListener(renderUpdateManager);
		voxelWorld.voxelOctree.addListener(new WorldUpdateListener(renderUpdateManager));
	}

	public void camera (Camera camera) {
		this.camera = camera;
	}

	public void environment (Environment environment) {
		chunkModelBatch.environment(environment);
	}

	public ChunkModelBatch chunkModelBatch () {
		return chunkModelBatch;
	}

	public ChunkModelBatch batch () {
		return chunkModelBatch;
	}

	public void render (float deltaTime) {
		voxelWorld.update();

		chunkModelBatch.begin(camera);
		chunkModelBatch.render();
		chunkModelBatch.end();
	}
}
