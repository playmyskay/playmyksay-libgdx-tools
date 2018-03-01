package com.playmyskay.voxel.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.common.OctreeTraversal.IntersectionData;
import com.playmyskay.voxel.actions.BoundingBoxIntersectionAction;
import com.playmyskay.voxel.actions.RunnerAction;
import com.playmyskay.voxel.actions.common.ActionData;
import com.playmyskay.voxel.common.VoxelWorld;
import com.playmyskay.voxel.face.VoxelPlaneTools;
import com.playmyskay.voxel.level.VoxelLevel;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class RenderManager {

	public enum UpdateType {
		add, remove
	}

	public static class UpdateData {
		public UpdateType type;
		public VoxelLevelChunk voxelLevelChunk;
	}

	public interface RenderableHandler {
		public void update (UpdateType type, ChunkRenderable chunkRenderable);
	}

	private VoxelMesher mesher = new VoxelMesher();
	private BoundingBox boundingBox = new BoundingBox(new Vector3(-100f, -100f, -100f), new Vector3(100f, 100f, 100f));
	private Set<VoxelLevelChunk> chunkSet = new HashSet<>();
	private HashMap<VoxelLevelChunk, ChunkRenderable> map = new HashMap<>();
	private ConcurrentLinkedQueue<UpdateData> updateQueue = new ConcurrentLinkedQueue<>();
	private int totalVertexCount = 0;
	private int renderedChunksCount = 0;
	private RenderableHandler renderableHandler;

	public int renderedChunksCount () {
		return renderedChunksCount;
	}

	public void renderableHandler (RenderableHandler renderableHandler) {
		this.renderableHandler = renderableHandler;
	}

	public void addUpdate (UpdateData updateData) {
		updateQueue.add(updateData);
	}

	public void sync () throws Exception {
		if (updateQueue.isEmpty()) return;
		int i = 0;
		UpdateData updateData = null;
		do {
			updateData = updateQueue.poll();
			if (updateData != null) {
				handleRenderable(updateData);
			}
			i++;
		} while (updateData != null && i < 64);
	}

	public void handleRenderable (UpdateData updateData) throws Exception {
		if (updateData.type == UpdateType.add) {
			ChunkRenderable chunkRenderable = map.get(updateData.voxelLevelChunk);

			if (chunkRenderable != null) {
				for (RenderableItem item : chunkRenderable.renderableItemList) {
					totalVertexCount -= item.mesh.getVertexSize();
				}

				chunkRenderable.renderableItemList.clear();

				if (updateData.voxelLevelChunk.voxelCompositeSet.size() == 0) {
					renderedChunksCount -= 1;
					map.remove(chunkRenderable.voxelLevelChunk);

					if (renderableHandler != null) {
						renderableHandler.update(UpdateType.remove, chunkRenderable);
					}

					renderedChunksCount--;
				}
			} else {
				if (updateData.voxelLevelChunk.voxelCompositeSet.size() > 0) {
					chunkRenderable = createChunkRenderable(updateData.voxelLevelChunk);
					if (chunkRenderable != null) {
						map.put(updateData.voxelLevelChunk, chunkRenderable);

						if (renderableHandler != null) {
							renderableHandler.update(UpdateType.add, chunkRenderable);
						}

						renderedChunksCount++;
					}
				}
			}
		}
	}

	public void updateChunks () {
		int chunkDepth = VoxelWorld.voxelWorld.voxelOctree.nodeProvider.depth(VoxelLevelChunk.class);
		Set<VoxelLevelChunk> newChunkSet = new HashSet<>();
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
			if (chunkSet.contains(chunk)) return;
			VoxelPlaneTools.determineVoxelPlaneFaces(chunk);

			UpdateData updateData = new UpdateData();
			updateData.type = UpdateType.add;
			updateData.voxelLevelChunk = chunk;
			addUpdate(updateData);
		});

		chunkSet.forEach(chunk -> {
			if (!newChunkSet.contains(chunk)) {
				chunkSet.remove(chunk);

				UpdateData updateData = new UpdateData();
				updateData.type = UpdateType.add;
				updateData.voxelLevelChunk = chunk;
				addUpdate(updateData);
			}
		});

		chunkSet.addAll(newChunkSet);
	}

	private ChunkRenderable createChunkRenderable (VoxelLevelChunk voxelLevelChunk) throws Exception {
		ChunkRenderable chunkRenderable = new ChunkRenderable();
		chunkRenderable.voxelLevelChunk = voxelLevelChunk;

		mesher.createMeshes(voxelLevelChunk, boundingBox, (voxelComposite, mesh) -> {
			RenderableItem renderableItem = new RenderableItem();
			renderableItem.mesh = mesh;

			switch (voxelComposite.voxelTypeDescriptor.voxelType) {
			case viewer:
				renderableItem.material = new Material(ColorAttribute.createDiffuse(Color.RED));
				break;
			case selection:
				renderableItem.material = new Material(ColorAttribute.createDiffuse(Color.GREEN));
				break;
			case undef:
			case voxel_static:
			default:
//				renderable.material = new Material(ColorAttribute.createDiffuse(new Color(MathUtils.random(0.2f, 1.0f),
//						MathUtils.random(0.2f, 1.0f), MathUtils.random(0.2f, 1.0f), 1f)));
				renderableItem.material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
				break;
			}

			chunkRenderable.renderableItemList.add(renderableItem);

			totalVertexCount += mesh.getNumVertices();
		});

		/*
		btIndexedMesh indexedMesh = new btIndexedMesh(renderable.mesh);
		btTriangleIndexVertexArray meshInterface = new btTriangleIndexVertexArray();
		meshInterface.addIndexedMesh(indexedMesh, PHY_ScalarType.PHY_SHORT);
		btVoxelCompositeShape meshShape = new btVoxelCompositeShape(meshInterface, voxelComposite);
		voxelComposite.collisionObject = new btCollisionObject();
		voxelComposite.collisionObject.setCollisionShape(meshShape);
		BulletInstance.getInstance().collisionWorld.addCollisionObject(voxelComposite.collisionObject);
		*/
		if (chunkRenderable.renderableItemList.isEmpty()) return null;

		return chunkRenderable;
	}
}
