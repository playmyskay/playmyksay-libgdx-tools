package com.playmyskay.octree.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.playmyskay.log.ILogger;
import com.playmyskay.octree.common.Octree;
import com.playmyskay.octree.common.OctreeNode;

public class OctreeDebugHelper<O extends Octree<N, ?>, N extends OctreeNode<N>> {

	private O octree;
	private ILogger logger;
	private int level = -1;

	public OctreeDebugHelper(O octree) {
		this(octree, -1);
	}

	public OctreeDebugHelper(O octree, int level) {
		this.octree = octree;
		this.level = level;
	}

	public void setLogger (ILogger logger) {
		this.logger = logger;
	}

	public ModelInstance createModelInstance () {
		return new ModelInstance(createModel());
	}

	public Model createModel () {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder builder = modelBuilder.part("line", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked,
				new Material());
		buildNode(builder, octree.rootNode, octree.curLevel);
		return modelBuilder.end();
	}

	private void buildNode (MeshPartBuilder builder, N node, int curLevel) {
		if (node == null) return;
		if (node.childs() != null) {
			for (N child : node.childs()) {
				if (child == null) continue;
				buildNode(builder, child, curLevel - 1);
			}
		}
		if (level == -1 || curLevel == level) {
			buildLines(builder, node, curLevel);
		}
	}

	private void buildLines (MeshPartBuilder builder, N node, int curLevel) {
		//int index = level.getIndex();

		float r = 0.2f;
		float g = 0.0f;
		float b = 0.0f;

		switch (curLevel) {
		case 0:
			r = 186;
			g = 225;
			b = 255;
			break;
		case 1:
			r = 65;
			g = 205;
			b = 244;
			break;
		case 2:
			r = 202;
			g = 65;
			b = 244;
			break;
		case 3:
			r = 186;
			g = 255;
			b = 201;
			break;
		case 4:
			r = 244;
			g = 149;
			b = 66;
			break;
		default:
			r = 255;
			g = 255;
			b = 255;
			break;
		}

		builder.setColor(new Color(r / 255f, g / 255f, b / 255f, 0.2f));
		BoxShapeBuilder.build(builder, node.boundingBox());
		if (logger != null) {
//			logger.log(String.format("level: %d / bb: min(%03.2f|%03.2f|%03.2f) max(%03.2f|%03.2f|%03.2f)", curLevel,
//					node.boundingBox().min.x, node.boundingBox().min.y, node.boundingBox().min.z,
//					node.boundingBox().max.x, node.boundingBox().max.y, node.boundingBox().max.z));
		}
	}

	public O getOctree () {
		return octree;
	}
}
