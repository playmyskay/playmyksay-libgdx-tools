package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class OctreeNode<N extends OctreeNode<N>> {
	static private BoundingBox rootBoundingBox = new BoundingBox();

	private N parent;

	public abstract void update (N node, OctreeNodeDescriptor descriptor);

	public boolean contains (Vector3 v, OctreeCalc calc) {
		return boundingBox(calc).contains(v);
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

	public abstract BoundingBox boundingBox ();

	public BoundingBox boundingBox (OctreeCalc calc) {
		if (hasBoundingBox()) return boundingBox();
		if (this == calc.octree().rootNode) return rootBoundingBox;
		return OctreeNodeTools.calcBoundingBoxFromNode(calc.boundingBox(), this, calc);
	}

	public int childIndex (OctreeNode<?> child) {
		int index = 0;
		for (N elem : childs()) {
			if (elem == child) return index;
			++index;
		}
		return -1;
	}

	public N child (int i) {
		if (childs() == null) return null;
		return childs()[i];
	}

	public N child (int i, N node) {
		return childs()[i] = node;
	}

	public abstract N[] childs ();

	public abstract N[] childs (N[] childs);

	public abstract boolean leaf ();

	public boolean hasChilds () {
		if (childs() == null) return false;
		for (int i = 0; i < 8; i++) {
			if (childs()[i] != null) return true;
		}
		return false;
	}

	public void descriptor (OctreeNodeDescriptor descriptor) {

	}
}
