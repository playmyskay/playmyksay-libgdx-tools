package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class OctreeCalc {

	private Octree<?, ?> octree;
	private int vectorIndex = 0;
	private int boundingBoxIndex = 0;
	private Vector3[] vectors = new Vector3[16];
	private BoundingBox[] boundingBoxs = new BoundingBox[8];
	private Array<OctreeNode<?>> nodeArray;

	private OctreeCalc child;

	public OctreeCalc() {
		for (int i = 0; i < vectors.length; ++i) {
			vectors[i] = new Vector3();
		}
		for (int i = 0; i < boundingBoxs.length; ++i) {
			boundingBoxs[i] = new BoundingBox();
		}
	}

	public OctreeCalc child () {
		if (child == null) {
			child = new OctreeCalc();
		}

		if (octree() != child.octree()) {
			child.octree(octree());
		}

		child.reset();
		return child;
	}

	public void octree (Octree<?, ?> octree) {
		this.octree = octree;
	}

	public Octree<?, ?> octree () {
		return octree;
	}

	public Vector3 vector () {
//		if (vectorIndex >= vectors.length) {
//			throw new GdxRuntimeException("no more vectors");
//		}
		return vectors[vectorIndex++];
	}

	public void reset () {
		vectorIndex = 0;
		boundingBoxIndex = 0;
	}

	public BoundingBox boundingBox () {
		return boundingBoxs[boundingBoxIndex++];
	}

	public Array<OctreeNode<?>> nodeArray (int capacity) {
		if (nodeArray == null) {
			nodeArray = new Array<>(true, capacity, OctreeNode.class);
		}

		if (nodeArray.items.length < capacity) {
			nodeArray = new Array<>(true, capacity, OctreeNode.class);
		}

		return nodeArray;
	}
}
