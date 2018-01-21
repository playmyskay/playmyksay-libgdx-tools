package com.playmyskay.octree;

import com.badlogic.gdx.math.Vector3;

public class OctreeTraversel {

	public static <N extends OctreeNode<N, ?>> N next (N node, Vector3 v) {
		if (!node.boundingBox.contains(v)) {
			return null;
		}

		for (int i = 0; i < 8; i++) {
			if (node.childs[i].contains(v)) {
				return node.childs[i];
			}
		}

		return null;
	}

	public static <N extends OctreeNode<N, ?>> N get (Octree<N, ?> octree, N node, Vector3 v) {
		N rootNode = octree.rootNode;
		if (!rootNode.boundingBox.contains(v)) {
			return null;
		}

		for (int level = octree.treeLevel; level >= node.getLevel(); --level) {
			node = next(node, v);
			if (node == null) {
				return null;
			}
		}

		return node;
	}

}
