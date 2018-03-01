package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class OctreeNode<N extends OctreeNode<N>> {
	private N parent;
	private N[] childs;
	private BoundingBox boundingBox = new BoundingBox();

	public boolean contains (Vector3 v) {
		return boundingBox.contains(v);
	}

	public N parent () {
		return parent;
	}

	public N parent (N parent) {
		return this.parent = parent;
	}

	public int depth () {
		int depth = 0;
		N node = parent;
		while (node != null) {
			depth++;
			node = node.parent();
		}
		return depth;
	}

	public BoundingBox boundingBox () {
		return boundingBox;
	}

	public N child (int i) {
		return childs[i];
	}

	public N child (int i, N node) {
		return childs[i] = node;
	}

	public N[] childs () {
		return childs;
	}

	public N[] childs (N[] childs) {
		return this.childs = childs;
	}

	public boolean hasChilds () {
		if (childs == null) return false;
		for (int i = 0; i < 8; i++) {
			if (childs[i] != null) return true;
		}
		return false;
	}

	public abstract void update (N node, OctreeNodeDescriptor descriptor);
}
