package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class OctreeNodeTools {

	public static <N extends OctreeNode<N>, D extends OctreeNodeDescriptor> N addNodeByVector (
			IOctreeNodeProvider<N> provider, N node, Vector3 v, D descriptor, BoundingBox tmpBoundingBox) {
		OctreeTools.adjustVector(v);
		N parentNode = null;
		N childNode = node;
		for (int level = provider.levelIndex(node.getClass()); level > 0 && childNode != null; --level) {
			parentNode = childNode;
			childNode = OctreeTools.contains(childNode, v);
			if (childNode != null) continue;

			for (int index = 0; index < 8 && childNode == null; ++index) {
				OctreeTools.calculateBounds(index, parentNode, tmpBoundingBox);
				if (tmpBoundingBox.contains(v)) {
					childNode = OctreeTools.createChild(provider, parentNode, level, index, tmpBoundingBox, descriptor);
				}
			}
		}

		return childNode;
	}

	public static <N extends OctreeNode<N>, D extends OctreeNodeDescriptor> N addNodeByBoundingBox (Octree<N, D> octree,
			N node, D descriptor, BoundingBox tmpBoundingBox) {
		N parentNode = null;
		N childNode = octree.rootNode;
		int levelIndex = octree.nodeProvider.levelIndex(node.getClass());
		for (int level = octree.curLevel; level > levelIndex && childNode != null; --level) {
			parentNode = childNode;
			childNode = OctreeTools.contains(childNode, node.boundingBox());
			if (childNode != null) continue;

			for (int index = 0; index < 8 && childNode == null; ++index) {
				OctreeTools.calculateBounds(index, parentNode, tmpBoundingBox);
				if (tmpBoundingBox.contains(node.boundingBox())) {
					childNode = OctreeTools.createChild(octree.nodeProvider, parentNode, level, index, tmpBoundingBox,
							descriptor);
				}
			}
		}

		if (childNode != null) {
			if (childNode == octree.rootNode) {
				octree.rootNode = node;
				return node;
			} else {
				if (parentNode != null) {
					int childIndex = parentNode.childIndex(childNode);
					parentNode.child(childIndex, node);
					return node;
				}
			}
		}

		return null;
	}

	public static <N extends OctreeNode<N>> int depth (OctreeNode<N> node) {
		int depth = 1;
		node = node.parent();
		while (node != null) {
			depth++;
			node = node.parent();
		}
		return depth;
	}

	public static <N extends OctreeNode<N>> void calculateBoundingBoxFromNodes (Array<OctreeNode<N>> nodes,
			BoundingBox boundingBox) {
		OctreeNode<N> parent = null;
		for (int i = 0; i < nodes.size - 1; ++i) {
			parent = nodes.items[i];
			OctreeTools.calculateBounds(parent.childIndex(nodes.items[i + 1]), parent, boundingBox);
		}
	}

	public static <N extends OctreeNode<N>> BoundingBox calcBoundingBoxFromRoot (OctreeNode<N> node,
			BoundingBox boundingBox) {
		int depth = depth(node);

		Array<OctreeNode<N>> nodes = new Array<OctreeNode<N>>(true, depth, OctreeNode.class);
		nodes.items[--depth] = node;

		OctreeNode<N> parent = null;
		while (depth > 0) {
			parent = nodes.items[depth].parent();
			nodes.insert(--depth, parent);
		}

		calculateBoundingBoxFromNodes(nodes, boundingBox);
		return boundingBox;
	}

	public static <N extends OctreeNode<N>> BoundingBox calcBoundingBoxFromNext (OctreeNode<N> node,
			BoundingBox boundingBox) {
		Array<OctreeNode<N>> nodes = new Array<OctreeNode<N>>(true, 4, OctreeNode.class);

		while (!node.hasBoundingBox()) {
			nodes.insert(0, node);
			node = node.parent();
		}
		nodes.insert(0, node);

		boundingBox.set(node.boundingBox());
		calculateBoundingBoxFromNodes(nodes, boundingBox);
		return boundingBox;
	}
}