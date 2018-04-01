package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class OctreeNode<N extends OctreeNode<N>> {
	static private BoundingBox rootBoundingBox = new BoundingBox();

	private N parent;
	private N[] childs;

	public abstract void update (N node, OctreeNodeDescriptor descriptor);

	public boolean contains (Vector3 v) {
		return boundingBox().contains(v);
	}

	public N parent () {
		return parent;
	}

	public N parent (N parent) {
		return this.parent = parent;
	}

	public boolean hasBoundingBox () {
		if (parent == null) return true;
		return false;
	}

	public BoundingBox boundingBox () {
		if (parent == null) return rootBoundingBox;

		//return calcBoundingBoxFromRoot(new BoundingBox());
		return OctreeNodeTools.calcBoundingBoxFromNext(this, new BoundingBox());
	}

	public int childIndex (OctreeNode<?> child) {
		int index = 0;
		for (N elem : childs) {
			if (elem == child) return index;
			++index;
		}
		return -1;
	}

	public N child (int i) {
		if (childs == null) return null;
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

	public void descriptor (OctreeNodeDescriptor descriptor) {

	}
}
