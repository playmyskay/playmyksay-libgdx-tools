package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelCache.TightMeshPool;
import com.badlogic.gdx.math.Vector3;
import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.level.VoxelLevelChunk;
import com.playmyskay.voxel.type.VoxelUsageType;
import com.playmyskay.voxel.world.VoxelWorld;

public class ChunkMesher {
	private static TightMeshPool meshPool = new TightMeshPool();

	public final static int VERTEX_SIZE_MAX = VoxelWorld.CHUNK_DIM * 6 * 6;
	public final static int INDEX_SIZE_MAX = Short.MAX_VALUE;

	public final static short[] indices = new short[INDEX_SIZE_MAX];
	public final static int[] indicesOrder = { 0, 1, 2, 2, 1, 3 };
	static {
		for (int ind = 0; ind < INDEX_SIZE_MAX; ++ind) {
			indices[ind] = (short) (indicesOrder[ind % 6] + (ind / 6) * 4);
		}
	}

	public final static VertexAttributes vertexAttributes = new VertexAttributes(VertexAttribute.Position(),
			VertexAttribute.Normal(), VertexAttribute.ColorPacked());

	public static void calculatePlaneMeshData (VoxelLevelChunk chunk, VoxelFacePlane plane, RenderableData rd) {
		Vector3 min = chunk.boundingBox().getMin(new Vector3());
		rd.voxelOffset().set(min.x, min.y, min.z);
		VoxelVerticesTools.createPlaneVertices(plane, rd.vertices(), rd.vertexCount(), rd.voxelOffset());

		rd.vertexCount(rd.vertices().size);
		rd.indexCount(rd.vertexCount() / 6 / 4 * 6);
		rd.material(determineMaterial(plane.descriptor.voxelType));
	}

	public static void calculateChunkMeshData (VoxelLevelChunk chunk, RenderableData rd) {
		rd.vertexCount(0);
		for (VoxelFacePlane plane : chunk.planeList) {
//			Direction direction = VoxelFace.getDirection(plane.faceBits);
			calculatePlaneMeshData(chunk, plane, rd);
		}
		rd.vertices().shrink();
	}

	private static Color previewColor = new Color(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b, 0.4f);

	public static Material determineMaterial (VoxelUsageType voxelType) {
		return new Material();
//		switch (voxelType) {
//		case viewer:
//			return new Material(ColorAttribute.createDiffuse(Color.RED));
//		case selection:
//			return new Material(ColorAttribute.createDiffuse(Color.GREEN));
//		case preview:
//			return new Material(ColorAttribute.createDiffuse(previewColor), new BlendingAttribute());
//		case undef:
//		case voxel_static:
//		default:
//			return new Material(ColorAttribute.createDiffuse(new Color(MathUtils.random(0.2f, 1.0f),
//					MathUtils.random(0.2f, 1.0f), MathUtils.random(0.2f, 1.0f), 1f)));
//		//			return new Material(ColorAttribute.createDiffuse(Color.WHITE));
//		}
	}

	public static Mesh createMesh (RenderableData rd) {
		if (rd.vertexCount() > 0) {
			Mesh mesh = meshPool.obtain(vertexAttributes, rd.vertexCount(), rd.indexCount());

			mesh.setVertices(rd.vertices().items, 0, rd.vertexCount());
			mesh.setIndices(indices, 0, rd.indexCount());

			return mesh;
		}
		return null;
	}

	//RenderInfoSystem.getInstance().setVoxelVerticesCount(totalVertexCount / 6);
	//RenderInfoSystem.getInstance().setRenderedChunksCount(renderedChunksCount);

	/* 	Ein Vertex besteht aus insgesamt 6 Einträgen (x y z nx ny nz). Daher erst / 6
	 	Eine Gruppe von 4 Vertices ergeben die Anzahl der Vertices, die innerhalb des Indexes benötigt werden. Daher / 4
	 		Oder auch anders ausgedrückt: Nach jedem vierten Element sind 6 Index-Einträge zu erstellen. Daher: * 6
			Hier gilt nämlich immer: 0 1 2 2 1 3 (plus offset natürlich)
			
	*/

}
