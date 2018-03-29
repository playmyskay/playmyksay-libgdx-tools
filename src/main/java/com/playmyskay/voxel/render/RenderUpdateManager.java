package com.playmyskay.voxel.render;

import java.util.HashMap;

import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class RenderUpdateManager {

	private HashMap<VoxelLevelChunk, ChunkRenderable> map = new HashMap<>();
	private RenderableHandler renderableHandler;

	public RenderUpdateManager(RenderableHandler renderableHandler) {
		this.renderableHandler = renderableHandler;
	}

	public void add (UpdateData updateData) {
		if (updateData == null) return;

		switch (updateData.type) {
		case addChunk:
			addChunk(updateData);
			break;
		case removeChunk:
			removeChunk(updateData);
			break;
		case addVoxel:
			addVoxel(updateData);
			break;
		case removeVoxel:
			removeVoxel(updateData);
		default:
			break;
		}
	}

	private void addChunk (UpdateData ud) {
		ChunkRenderable chunkRenderable = map.get(ud.voxelLevelChunk);
		if (chunkRenderable != null) {
			if (ud.voxelLevelChunk.voxelCompositeSet.size() == 0) {
				removeChunk(ud);
			}
		} else {
			if (ud.voxelLevelChunk.voxelCompositeSet.size() > 0) {
				chunkRenderable = new ChunkRenderable();
				chunkRenderable.voxelLevelChunk = ud.voxelLevelChunk;
				chunkRenderable.updateDataQueue.add(ud);
				map.put(ud.voxelLevelChunk, chunkRenderable);

				ud.renderableDatas = new RenderableData[ud.voxelLevelChunk.voxelCompositeSet.size()];
				for (int i = 0; i < ud.voxelLevelChunk.voxelCompositeSet.size(); ++i) {
					ud.renderableDatas[i] = new RenderableData();
				}

				// calculate the whole chunk mesh data to prepare the final mesh instance
				Mesher.calculateChunkMeshData(chunkRenderable.voxelLevelChunk, ud.renderableDatas);

				renderableHandler.update(chunkRenderable);
			}
		}
	}

	private void removeChunk (UpdateData ud) {
		ChunkRenderable chunkRenderable = map.get(ud.voxelLevelChunk);
		if (chunkRenderable != null) {
			chunkRenderable.updateDataQueue.add(ud);
			map.remove(chunkRenderable.voxelLevelChunk);

			renderableHandler.update(chunkRenderable);
		}
	}

	private void addVoxel (UpdateData ud) {
		ChunkRenderable chunkRenderable = map.get(ud.voxelLevelChunk);
		if (chunkRenderable != null) {
			ud.renderableDatas = new RenderableData[1];
			ud.renderableDatas[0] = new RenderableData();

			VoxelComposite voxelComposite = chunkRenderable.voxelLevelChunk.getVoxelComposite(ud.voxelLevelEntity);
			ud.renderableDatas[0].userData(voxelComposite);
			Mesher.calculateCompositeMeshData(chunkRenderable.voxelLevelChunk, voxelComposite, ud.renderableDatas[0]);

			chunkRenderable.updateDataQueue.add(ud);
			renderableHandler.update(chunkRenderable);
		}
	}

	private void removeVoxel (UpdateData ud) {
		ChunkRenderable chunkRenderable = map.get(ud.voxelLevelChunk);
		if (chunkRenderable != null) {
			VoxelComposite voxelComposite = chunkRenderable.voxelLevelChunk.getVoxelComposite(ud.voxelLevelEntity);

			ud.renderableDatas = new RenderableData[1];
			ud.renderableDatas[0] = new RenderableData();

			if (voxelComposite != null) {
				// update VoxelComposite
				ud.renderableDatas[0].userData(voxelComposite);
				Mesher.calculateCompositeMeshData(chunkRenderable.voxelLevelChunk, voxelComposite,
						ud.renderableDatas[0]);
			} else {
				ud.renderableDatas[0].userData(ud.voxelLevelEntity);
			}

			chunkRenderable.updateDataQueue.add(ud);
			renderableHandler.update(chunkRenderable);
		}
	}
}