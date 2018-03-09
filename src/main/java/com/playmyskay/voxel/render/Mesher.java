package com.playmyskay.voxel.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelCache.TightMeshPool;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.playmyskay.voxel.common.VoxelComposite;
import com.playmyskay.voxel.common.VoxelWorld;
import com.playmyskay.voxel.face.VoxelFacePlane;
import com.playmyskay.voxel.face.VoxelFaceTools;
import com.playmyskay.voxel.level.VoxelLevelChunk;

public class Mesher {
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
			VertexAttribute.Normal());

	public static void calculateCompositeMeshData (VoxelLevelChunk chunk, VoxelComposite voxelComposite,
			RenderableData rd) {
		rd.vertexCount(0);
		Vector3 min = chunk.boundingBox().getMin(new Vector3());
		for (VoxelFacePlane plane : voxelComposite.planeList) {
			rd.voxelOffset().set(min.x, min.y, min.z);
			rd.vertexCount(VoxelFaceTools.createFaceVertices(plane, rd.vertices(), rd.vertexCount(), rd.voxelOffset()));
		}

		// for better understanding: without reducing
		rd.indexCount(rd.vertexCount() / 6 / 4 * 6);

		rd.material(determineMaterial(voxelComposite));
	}

	public static void calculateChunkMeshData (VoxelLevelChunk chunk, RenderableData[] renderableDatas) {
		int i = 0;
		for (VoxelComposite voxelComposite : chunk.voxelCompositeSet) {
			calculateCompositeMeshData(chunk, voxelComposite, renderableDatas[i++]);
		}
	}

	private static Color previewColor = new Color(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b, 0.1f);

	public static Material determineMaterial (VoxelComposite voxelComposite) {
		switch (voxelComposite.voxelTypeDescriptor.voxelType) {
		case viewer:
			return new Material(ColorAttribute.createDiffuse(Color.RED));
		case selection:
			return new Material(ColorAttribute.createDiffuse(Color.GREEN));
		case preview:
			return new Material(ColorAttribute.createDiffuse(previewColor));
		case undef:
		case voxel_static:
		default:
//					renderable.material = new Material(ColorAttribute.createDiffuse(new Color(MathUtils.random(0.2f, 1.0f),
//							MathUtils.random(0.2f, 1.0f), MathUtils.random(0.2f, 1.0f), 1f)));
			return new Material(ColorAttribute.createDiffuse(Color.WHITE));
		}
	}

	public static Mesh createMesh (RenderableData rd) {
		if (rd.vertexCount() > 0) {
			Mesh mesh = meshPool.obtain(vertexAttributes, rd.vertexCount(), rd.indexCount());

			mesh.setVertices(rd.vertices(), 0, rd.vertexCount());
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
