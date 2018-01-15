package com.playmyskay.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class DebugGridCreator {

	public static void line (MeshPartBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2,
			float r, float g, float b, float a) {
		builder.setColor(new Color(r, g, b, a));
		builder.line(new Vector3(x1, y1, z1), new Vector3(x2, y2, z2));
	}

	public static void grid (MeshPartBuilder builder, int width, int height) {
		for (int x = -width / 2; x <= width / 2; x++) {
			// vertical
			line(builder, x, 0, 1f * -height / 2f, x, 0, height / 2f, 0.3f, 0.3f, 0.3f, 0.0f);
		}

		for (int y = -height / 2; y <= height / 2; y++) {
			// horizontal
			line(builder, -width / 2, 0, -y, width / 2, 0, -y, 0.3f, 0.3f, 0.3f, 0.0f);
		}
	}

	public static ModelInstance create (int x, int y) {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder builder = modelBuilder.part("line", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked,
				new Material());

		grid(builder, x, y);

		return new ModelInstance(modelBuilder.end());
	}
}
