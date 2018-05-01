package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class OctreeNodeTools {

//	public static <N extends OctreeNode<N>, D extends OctreeNodeDescriptor> N addNodeByVector (
//			IOctreeNodeProvider<N> provider, N node, Vector3 v, D descriptor, OctreeCalc calc) {
//
//		calc = calc.child();
//
//		OctreeTools.adjustVector(v);
//		N parentNode = null;
//		N childNode = node;
//		BoundingBox boundingBox = calc.boundingBox();
//		Vector3 corner = calc.vector();
//		Vector3 cnt = calc.vector();
//		for (int level = provider.levelIndex(node.getClass()); level > 0 && childNode != null; --level) {
//			parentNode = childNode;
//			childNode = OctreeTools.contains(childNode, v, calc);
//			calc.reset();
//			if (childNode != null) continue;
//
//			for (int index = 0; index < 8 && childNode == null; ++index) {
//				OctreeTools.calculateBounds(boundingBox, index, parentNode, corner, cnt, calc);
//				if (boundingBox.contains(v)) {
//					childNode = OctreeTools.createChild(provider, parentNode, level, index, boundingBox, descriptor);
//				}
//
//				calc.reset();
//			}
//
//			calc.reset();
//		}
//
//		return childNode;
//	}

	public static <N extends OctreeNode<N>, D extends OctreeNodeDescriptor> N addNodeByVector (
			IOctreeNodeProvider<N> provider, N node, Vector3 v, D descriptor, OctreeCalc calc) {

		calc = calc.child();

		OctreeTools.adjustVector(v);
		N parentNode = null;
		N childNode = node;
		BoundingBox boundingBox = calc.boundingBox();
		Vector3 corner = calc.vector();
		Vector3 cnt = calc.vector();
		for (int level = provider.levelIndex(node.getClass()); level > 0 && childNode != null; --level) {
			parentNode = childNode;
			childNode = OctreeTools.contains(childNode, v, calc);
			calc.reset();
			if (childNode != null) continue;

			for (int index = 0; index < 8 && childNode == null; ++index) {
				OctreeTools.calculateBounds(boundingBox, index, parentNode, corner, cnt, calc);
				if (boundingBox.contains(v)) {
					childNode = OctreeTools.createChild(provider, parentNode, level, index, boundingBox, descriptor);
				}

				calc.reset();
			}

			calc.reset();
		}

		return childNode;
	}

	public static synchronized <N extends OctreeNode<N>, D extends OctreeNodeDescriptor> N addNodeByBoundingBox (
			Octree<N, D> octree, N node, D descriptor, OctreeCalc calc) {
		N parentNode = null;
		N childNode = octree.rootNode;
		int levelIndex = octree.nodeProvider.levelIndex(node.getClass());

		BoundingBox boundingBox = calc.boundingBox();
		Vector3 corner = calc.vector();
		Vector3 cnt = calc.vector();
		for (int level = octree.curLevel; level > levelIndex && childNode != null; --level) {
			parentNode = childNode;
			childNode = OctreeTools.contains(childNode, node.boundingBox());
			if (childNode != null) continue;

			for (int index = 0; index < 8 && childNode == null; ++index) {
				OctreeTools.calculateBounds(boundingBox, index, parentNode, corner, cnt, calc);
				if (boundingBox.contains(node.boundingBox())) {
					childNode = OctreeTools.createChild(octree.nodeProvider, parentNode, level, index, boundingBox,
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
					node.parent(parentNode);
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

	public static <N extends OctreeNode<N>> void calculateBoundingBoxFromNodes (BoundingBox boundingBox,
			Array<OctreeNode<?>> nodes, int startIndex, OctreeCalc calc) {
		OctreeNode<?> parent = null;

		Vector3 corner = calc.vector();
		Vector3 cnt = calc.vector();
		for (int i = startIndex; i > 0 && nodes.items[i] != null; --i) {
			parent = nodes.items[i];
			OctreeTools.calculateBounds(boundingBox, parent.childIndex(nodes.items[i - 1]), parent, corner, cnt, calc);
		}
	}

	public static <N extends OctreeNode<N>> BoundingBox calcBoundingBoxFromRoot (BoundingBox boundingBox,
			OctreeNode<N> node, OctreeCalc calc) {
		int depth = depth(node);

		Array<OctreeNode<?>> nodes = calc.nodeArray(depth);

		int index = depth;
		nodes.items[--index] = node;

		OctreeNode<?> parent = null;
		while (index > 0) {
			parent = nodes.items[depth].parent();
			nodes.insert(--index, parent);
		}

		calculateBoundingBoxFromNodes(boundingBox, nodes, depth - 1, calc);
		return calc.boundingBox();
	}

	public static <N extends OctreeNode<N>> BoundingBox calcBoundingBoxFromNode (BoundingBox boundingBox,
			OctreeNode<N> node, OctreeCalc calc) {
		calc = calc.child();

		Array<OctreeNode<?>> nodes = calc.nodeArray(7);
		nodes.clear();

		int i = 0;
		while (!node.hasBoundingBox()) {
			nodes.items[i++] = node;
			node = node.parent();
		}
		nodes.items[i] = node;

		boundingBox.set(node.boundingBox());
		calculateBoundingBoxFromNodes(boundingBox, nodes, i, calc);

		return boundingBox;
	}
}
