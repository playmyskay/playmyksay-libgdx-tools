package com.playmyskay.octree.traversal;

import com.badlogic.gdx.math.Vector3;
import com.playmyskay.octree.common.OctreeNode;

public class IntersectionData<N extends OctreeNode<N>> {
	public N node;
	public Vector3 point;
	public Vector3 normal;
}