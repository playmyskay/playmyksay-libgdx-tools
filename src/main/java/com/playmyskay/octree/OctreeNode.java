package com.playmyskay.octree;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class OctreeNode<N extends OctreeNode<N, D>, D extends OctreeNodeDescriptor> {
	public N parent;
	public N[] childs;
	public byte index = 0xF;
	public BoundingBox boundingBox = new BoundingBox();

	public boolean contains (Vector3 v) {
		return boundingBox.contains(v);
	}

	public int getLevel () {
		int level = 0;
		N node = parent;
		while (node != null) {
			level++;
			node = node.parent;
		}
		return level;
	}

	public abstract void update (N node, D descriptor);
}
