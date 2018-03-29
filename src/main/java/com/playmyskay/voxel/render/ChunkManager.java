package com.playmyskay.voxel.render;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.traversal.IntersectionData;
import com.playmyskay.voxel.actions.BoundingBoxIntersectionAction;
import com.playmyskay.voxel.actions.RunnerAction;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.world.VoxelWorld;

public class ChunkManager {

	private BoundingBox boundingBox = new BoundingBox(new Vector3(-100f, -100f, -100f), new Vector3(100f, 100f, 100f));
	private Set<VoxelLevelChunk> visibleChunkSet = new HashSet<>();
	private Set<VoxelLevelChunk> curChunkSet = new HashSet<>();
	private RenderUpdateManager updateManager;
	private VoxelWorld voxelWorld;

	public ChunkManager(VoxelWorld voxelWorld, RenderUpdateManager renderableManager) {
		this.voxelWorld = voxelWorld;
		this.updateManager = renderableManager;
	}

	public void updateVisibleChunks () {
		curChunkSet.clear();

		int chunkDepth = voxelWorld.voxelOctree.nodeProvider.depth(VoxelLevelChunk.class);

		RunnerAction runner = new RunnerAction();
		runner.add(new BoundingBoxIntersectionAction(new Integer[] { chunkDepth }, boundingBox, chunkDepth));

		ActionData actionData = new ActionData();
		actionData.octree(voxelWorld.voxelOctree);
		runner.run(actionData);
		if (actionData.intersectionDataList() != null && !actionData.intersectionDataList().isEmpty()) {
			for (IntersectionData<VoxelLevel> intersectionData : actionData.intersectionDataList()) {
				if (intersectionData.node == null || !(intersectionData.node instanceof VoxelLevelChunk)) continue;
				curChunkSet.add((VoxelLevelChunk) intersectionData.node);
			}
		}

		curChunkSet.forEach(chunk -> {
			if (visibleChunkSet.contains(chunk)) return;

			chunk.rebuild();

			UpdateData updateData = new UpdateData();
			updateData.type = UpdateType.addChunk;
			updateData.voxelLevelChunk = chunk;
			updateManager.add(updateData);
		});

		visibleChunkSet.parallelStream().forEach(chunk -> {
			if (!curChunkSet.contains(chunk)) {
				UpdateData updateData = new UpdateData();
				updateData.type = UpdateType.removeChunk;
				updateData.voxelLevelChunk = chunk;
				updateManager.add(updateData);
			}
		});

		visibleChunkSet.clear();
		visibleChunkSet.addAll(curChunkSet);
	}
}
