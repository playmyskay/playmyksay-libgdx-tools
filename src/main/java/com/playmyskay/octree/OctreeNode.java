package com.playmyskay.octree;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class OctreeNode {
	public OctreeNode parent;
	public OctreeNode[] childs;
	public BoundingBox boundingBox = new BoundingBox();

	public boolean contains (Vector3 v) {
		return boundingBox.contains(v);
	}
}
