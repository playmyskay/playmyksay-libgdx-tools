package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelCache.TightMeshPool;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.common.VoxelOffset;
import com.playmyskay.voxel.common.VoxelWorld;
import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.face.VoxelFaceTools;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class VoxelMesher {
	private TightMeshPool meshPool = new TightMeshPool();

	public final static int VERTEX_SIZE_MAX = VoxelWorld.CHUNK_DIM * 6 * 6;
	public final static int INDEX_SIZE_MAX = Short.MAX_VALUE;

	float[] vertices = new float[VERTEX_SIZE_MAX];

	public final static short[] indices = new short[INDEX_SIZE_MAX];
	public final static int[] indicesOrder = { 0, 1, 2, 2, 1, 3 };
	static {
		for (int ind = 0; ind < INDEX_SIZE_MAX; ++ind) {
			indices[ind] = (short) (indicesOrder[ind % 6] + (ind / 6) * 4);
		}
	}

	public final static VertexAttributes vertexAttributes = new VertexAttributes(VertexAttribute.Position(),
			VertexAttribute.Normal());

	int vertexCount = 0;
	int indexCount = 0;
	float dst1 = 0f;
	float dst2 = 0f;
	VoxelOffset voxelOffset = new VoxelOffset();
	Vector3 cnt = new Vector3();
	Vector3 tmp = new Vector3();

	public interface MeshHandler {
		public void handle (VoxelComposite voxelComposite, Mesh mesh);
	}

	public void createMeshes (VoxelLevelChunk chunk, BoundingBox boundingBox, MeshHandler meshHandler)
			throws Exception {
		for (VoxelComposite voxelComposite : chunk.voxelCompositeSet) {
			vertexCount = 0;
			for (VoxelFacePlane plane : voxelComposite.planeList) {
				boundingBox.getCenter(cnt);
				dst1 = cnt.dst2(tmp.set(plane.x1, plane.y1, plane.z1));
				dst2 = cnt.dst2(tmp.set(plane.x2, plane.y2, plane.z2));

				if (dst1 <= dst2) {
					voxelOffset.set(plane.x1, plane.y1, plane.z1);
				} else {
					voxelOffset.set(plane.x2, plane.y2, plane.z2);
				}

				vertexCount += VoxelFaceTools.createFaceVertices(plane, vertices, vertexCount, voxelOffset);
			}

			// for better understanding: without reducing
			indexCount = vertexCount / 6 / 4 * 6;

			if (vertexCount > 0) {
				Mesh mesh = meshPool.obtain(vertexAttributes, vertexCount, indexCount);

				mesh.setVertices(vertices, 0, vertexCount);
				mesh.setIndices(indices, 0, indexCount);

				meshHandler.handle(voxelComposite, mesh);
			}
		}

		//RenderInfoSystem.getInstance().setVoxelVerticesCount(totalVertexCount / 6);
		//RenderInfoSystem.getInstance().setRenderedChunksCount(renderedChunksCount);

		/* 	Ein Vertex besteht aus insgesamt 6 Einträgen (x y z nx ny nz). Daher erst / 6
		 	Eine Gruppe von 4 Vertices ergeben die Anzahl der Vertices, die innerhalb des Indexes benötigt werden. Daher / 4
		 		Oder auch anders ausgedrückt: Nach jedem vierten Element sind 6 Index-Einträge zu erstellen. Daher: * 6
				Hier gilt nämlich immer: 0 1 2 2 1 3 (plus offset natürlich)
				
		*/

	}

}
