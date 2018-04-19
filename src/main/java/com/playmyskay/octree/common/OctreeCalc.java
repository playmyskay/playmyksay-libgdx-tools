package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class OctreeCalc {
	private Vector3 vector = new Vector3();
	private Vector3 vector2 = new Vector3();
	private BoundingBox boundingBox = new BoundingBox();
	private Array<OctreeNode<?>> nodeArray;

	public Vector3 vector () {
		return vector;
	}

	public Vector3 vector2 () {
		return vector2;
	}

	public BoundingBox boundingBox () {
		return boundingBox;
	}

	public Array<OctreeNode<?>> nodeArray (int capacity) {
		if (nodeArray == null) {
			nodeArray = new Array<>(true, capacity, OctreeNode.class);
		}

		if (nodeArray.size != capacity) {
			nodeArray.setSize(capacity);
		}

		return nodeArray;
	}
}
