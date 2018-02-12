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

	public int getDepth () {
		int depth = 0;
		N node = parent;
		while (node != null) {
			depth++;
			node = node.parent;
		}
		return depth;
	}

	public boolean hasChilds () {
		if (childs == null) return false;
		for (int i = 0; i < 8; i++) {
			if (childs[i] != null) return true;
		}
		return false;
	}

	public abstract void update (N node, D descriptor);
}
