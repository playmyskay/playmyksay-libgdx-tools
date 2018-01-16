package com.playmyskay.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class GridCreator {
	private ModelBuilder modelBuilder = new ModelBuilder();
	private MeshPartBuilder builder;
	private Vector3 pos = new Vector3();
	private Color color = new Color(0.5f, 0.5f, 0.5f, 1f);

	private GridCreator() {
		modelBuilder.begin();
		builder = modelBuilder.part("line", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
	}

	public static GridCreator create () {
		return new GridCreator();
	}

	public ModelInstance finish () {
		return new ModelInstance(modelBuilder.end());
	}

	private static void line (MeshPartBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2,
			Color color) {
		builder.setColor(color);
		builder.line(new Vector3(x1, y1, z1), new Vector3(x2, y2, z2));
	}

	private static void grid (MeshPartBuilder builder, float x1, float x2, float y1, float y2, float z1, float z2,
			Color color) {
		if (x1 < x2) {
			for (float x = x1; x <= x2; x++) {
				line(builder, x, y1, z1, x, y2, z2, color);
			}
		}
		if (y1 < y2) {
			for (float y = y1; y <= y2; y++) {
				line(builder, x1, y, z1, x2, y, z2, color);
			}
		}
		if (z1 < z2) {
			for (float z = z1; z <= z2; z++) {
				line(builder, x1, y1, z, x2, y2, z, color);
			}
		}
	}

	public GridCreator pos (float x, float y, float z) {
		pos.set(x, y, z);
		return this;
	}

	public GridCreator color (float r, float g, float b, float a) {
		color.set(r, g, b, a);
		return this;
	}

	public GridCreator grid (float size) {
		gridXY(size);
		gridXZ(size);
		gridYZ(size);
		return this;
	}

	public GridCreator gridXY (float size) {
		grid(builder, x1(size), x2(size), y1(size), y2(size), pos.z, pos.z, color);
		return this;
	}

	public GridCreator gridXZ (float size) {
		grid(builder, x1(size), x2(size), pos.y, pos.y, z1(size), z2(size), color);
		return this;
	}

	public GridCreator gridYZ (float size) {
		grid(builder, pos.x, pos.x, y1(size), y2(size), z1(size), z2(size), color);
		return this;
	}

	private float x1 (float size) {
		return pos.x - size * 0.5f;
	}

	private float x2 (float size) {
		return pos.x + size * 0.5f;
	}

	private float y1 (float size) {
		return pos.y - size * 0.5f;
	}

	private float y2 (float size) {
		return pos.y + size * 0.5f;
	}

	private float z1 (float size) {
		return pos.z - size * 0.5f;
	}

	private float z2 (float size) {
		return pos.z + size * 0.5f;
	}
}
