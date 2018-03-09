package com.playmyskay.voxel.render;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.traversal.IntersectionData;
import com.playmyskay.voxel.actions.BoundingBoxIntersectionAction;
import com.playmyskay.voxel.actions.RunnerAction;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.common.VoxelWorld;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class ChunkManager {

	private BoundingBox boundingBox = new BoundingBox(new Vector3(-100f, -100f, -100f), new Vector3(100f, 100f, 100f));
	private Set<VoxelLevelChunk> visibleChunkSet = new HashSet<>();
	private Set<VoxelLevelChunk> newChunkSet = new HashSet<>();
	private RenderUpdateManager updateManager;

	public ChunkManager(RenderUpdateManager renderableManager) {
		this.updateManager = renderableManager;
	}

	public void updateVisibleChunks () {
		newChunkSet.clear();

		int chunkDepth = VoxelWorld.voxelWorld.voxelOctree.nodeProvider.depth(VoxelLevelChunk.class);

		RunnerAction runner = new RunnerAction();
		runner.add(new BoundingBoxIntersectionAction(new Integer[] { chunkDepth }, boundingBox, chunkDepth));

		ActionData actionData = new ActionData();
		actionData.octree(VoxelWorld.voxelWorld.voxelOctree);
		runner.run(actionData);
		if (actionData.intersectionDataList() != null && !actionData.intersectionDataList().isEmpty()) {
			for (IntersectionData<VoxelLevel> intersectionData : actionData.intersectionDataList()) {
				if (intersectionData.node == null || !(intersectionData.node instanceof VoxelLevelChunk)) continue;
				newChunkSet.add((VoxelLevelChunk) intersectionData.node);
			}
		}

		newChunkSet.parallelStream().forEach(chunk -> {
			if (visibleChunkSet.contains(chunk)) return;

			UpdateData updateData = new UpdateData();
			updateData.type = UpdateType.addChunk;
			updateData.voxelLevelChunk = chunk;
			updateManager.add(updateData);
		});

		visibleChunkSet.forEach(chunk -> {
			if (!newChunkSet.contains(chunk)) {
				visibleChunkSet.remove(chunk);

				UpdateData updateData = new UpdateData();
				updateData.type = UpdateType.removeChunk;
				updateData.voxelLevelChunk = chunk;
				updateManager.add(updateData);
			}
		});

		visibleChunkSet.addAll(newChunkSet);
	}
}
