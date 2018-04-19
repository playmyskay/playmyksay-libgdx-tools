package com.playmyskay.voxel.render;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.playmyskay.voxel.world.IChunkUpdateListener;
import com.playmyskay.voxel.world.UpdateData;

class RenderUpdateManager implements IChunkUpdateListener {
//	private HashMap<VoxelLevelChunk, ChunkRenderable> map = new HashMap<>();
	private IRenderableHandler renderableHandler;
	private ConcurrentLinkedQueue<RenderUpdateData> updateQueue = new ConcurrentLinkedQueue<>();

	public RenderUpdateManager(IRenderableHandler renderableHandler) {
		this.renderableHandler = renderableHandler;

		new Thread(new Runnable() {
			@Override
			public void run () {
				while (true) {
					process();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void add (UpdateData updateData) {
		if (updateData == null) return;
		if (!(updateData instanceof RenderUpdateData)) return;

		RenderUpdateData renderUpdateData = (RenderUpdateData) updateData;
		updateQueue.offer(renderUpdateData);
	}

	private void process () {
		while (!updateQueue.isEmpty()) {
			RenderUpdateData updateData = updateQueue.poll();
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

	}

//	private ChunkRenderable getChunkRenderable (VoxelLevelChunk chunk) {
//		ChunkRenderable chunkRenderable = map.get(chunk);
//		return chunkRenderable;
//	}
//
//	private ChunkRenderable createChunkRenderable (VoxelLevelChunk chunk) {
//		ChunkRenderable chunkRenderable = new ChunkRenderable();
//		chunkRenderable.voxelLevelChunk = chunk;
//		map.put(chunk, chunkRenderable);
//		return chunkRenderable;
//	}
//
//	private void removeChunkRenderable (ChunkRenderable chunkRenderable) {
//		map.remove(chunkRenderable.voxelLevelChunk);
//	}

	private void addChunk (RenderUpdateData ud) {
		ChunkRenderable chunkRenderable = null;//getChunkRenderable(ud.voxelLevelChunk);
//		if (chunkRenderable != null) {
//			if (ud.voxelLevelChunk.planeList.size == 0) {
//				removeChunk(ud);
//			}
//		} else {
		if (ud.voxelLevelChunk.planeList.size == 0) return;

//			chunkRenderable = createChunkRenderable(ud.voxelLevelChunk);
//			chunkRenderable.voxelLevelChunk = ud.voxelLevelChunk;
//			chunkRenderable.renderableData = new RenderableData();
//			ud.renderableData = chunkRenderable.renderableData;
		ud.renderableData = new RenderableData();

		// calculate the whole chunk mesh data to prepare the final mesh instance
		ChunkMesher.calculateChunkMeshData(ud.voxelWorld, ud.voxelLevelChunk, ud.renderableData);

		renderableHandler.update(ud);
//		}
	}

	private void removeChunk (RenderUpdateData ud) {
		renderableHandler.update(ud);

//		ChunkRenderable chunkRenderable = getChunkRenderable(ud.voxelLevelChunk);
//		if (chunkRenderable != null) {
//			removeChunkRenderable(chunkRenderable);
//			renderableHandler.update(ud);
//		}
	}

	private void addVoxel (RenderUpdateData ud) {
//		ChunkRenderable chunkRenderable = map.get(ud.voxelLevelChunk);
//		if (chunkRenderable != null) {
//			ud.renderableData = new RenderableData();
//			ud.renderableData.userData(ud.voxelLevelEntity);
////			Mesher.calculatePlaneMeshData(chunkRenderable.voxelLevelChunk, ud.renderableDatas[0]);
//
////			chunkRenderable.updateDataQueue.add(ud);
////			renderableHandler.update(chunkRenderable);
//		}
	}

	private void removeVoxel (RenderUpdateData ud) {
//		ChunkRenderable chunkRenderable = getChunkRenderable(ud.voxelLevelChunk);
//		if (chunkRenderable != null) {
////			VoxelComposite voxelComposite = chunkRenderable.voxelLevelChunk.getVoxelComposite(ud.voxelLevelEntity);
////
////			ud.renderableDatas = new RenderableData[1];
////			ud.renderableDatas[0] = new RenderableData();
////
////			if (voxelComposite != null) {
////				// update VoxelComposite
////				ud.renderableDatas[0].userData(voxelComposite);
////				Mesher.calculateCompositeMeshData(chunkRenderable.voxelLevelChunk, voxelComposite,
////						ud.renderableDatas[0]);
////			} else {
////				ud.renderableDatas[0].userData(ud.voxelLevelEntity);
////			}
////
////			chunkRenderable.updateDataQueue.add(ud);
////			renderableHandler.update(chunkRenderable);
//		}
	}

	@Override
	public UpdateData create () {
		return new RenderUpdateData();
	}
}