package com.playmyskay.octree;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.OctreeNodeDescriptor.Type;

public class Octree<N extends OctreeNode<N, D>, D extends OctreeNodeDescriptor> {
	public int curLevel = 0;
	public N rootNode;
	public IOctreeNodeProvider<N> nodeProvider;
	private Vector3[] corners = new Vector3[8];
	private float[] dst2 = new float[8];
	private Vector3 tmp = new Vector3();

	public Octree(IOctreeNodeProvider<N> nodeProvider) {
		this.nodeProvider = nodeProvider;
		rootNode = nodeProvider.create(0);
		rootNode.boundingBox.set(new Vector3(), new Vector3(1f, 1f, 1f));

		for (int i = 0; i < 8; i++) {
			corners[i] = new Vector3();
		}
	}

	public N setNode (Vector3 v, D descriptor) {
		if (!rootNode.boundingBox.contains(v)) {
			if (descriptor.type == Type.remove) {
				return null;
			}

			Vector3 min = new Vector3();
			Vector3 max = new Vector3();
			while (!rootNode.boundingBox.contains(v)) {
				int near = OctreeTools.getNearestIndex(rootNode.boundingBox, v, corners, dst2);
				int far = 7 - near;

				// min is the farest corner
				min.set(corners[far]);

				/* 
				* Equivalent to tmp.set(corners[near]).sub(corners[far]).scl(2f);
				* Vector3 t1 = new Vector3().set(corners[near]);
				* Vector3 t2 = new Vector3().set(corners[far]);
				* Vector3 t3 = new Vector3().set(t1).sub(t2).scl(2f); 
				*/
				tmp.set(corners[near]).sub(corners[far]).scl(2f);
				max.set(min).add(tmp);

				N newRootNode = nodeProvider.create(curLevel + 1);
				newRootNode.boundingBox.set(min, max);

				// The new enclosing root node sets the current root node as its child.
				// The index is visually the nearest node of its origin expansion direction.
				newRootNode.childs = nodeProvider.createArray(curLevel, 8);
				newRootNode.childs[far] = rootNode;
				rootNode.parent = newRootNode;
				rootNode.index = (byte) far;
				rootNode = newRootNode;

				curLevel++;
			}
		}

		N updateNode = processDescriptor(v, descriptor);
		updateNode(updateNode, descriptor);

		return updateNode;
	}

	private N addNode (Vector3 v, D descriptor) {
		OctreeTools.adjustVector(v);

		N lastNode = null;
		N currentNode = rootNode;
		BoundingBox boundingBox = new BoundingBox();
		for (int level = curLevel; level > 0 && currentNode != null; --level) {
			lastNode = currentNode;
			currentNode = next(v, currentNode, descriptor, boundingBox);
			if (currentNode != null) continue;

			for (int index = 0; index < 8; index++) {
				OctreeTools.calculateBounds(index, lastNode, boundingBox);
				if (boundingBox.contains(v)) {
					currentNode = createChild(lastNode, level, index, boundingBox);
				}
			}
		}
		return currentNode;
	}

	public N removeNode (Vector3 v, D descriptor) {
		OctreeTools.adjustVector(v);

		N lastNode = null;
		N currentNode = rootNode;
		BoundingBox boundingBox = new BoundingBox();
		for (int level = curLevel; level > 0 && currentNode != null; --level) {
			lastNode = currentNode;
			currentNode = next(v, currentNode, descriptor, boundingBox);
		}

		if (currentNode == null && lastNode != null) {
			currentNode = lastNode;
		}

		lastNode = currentNode;

		while (currentNode != null && currentNode.parent != null) {
			deleteChild(currentNode);
			if (currentNode.parent.hasChilds()) break;

			currentNode = currentNode.parent;
		}

		return lastNode;
	}

	private N processDescriptor (Vector3 v, D descriptor) {
		switch (descriptor.type) {
		case add:
			return addNode(v, descriptor);
		case remove:
			return removeNode(v, descriptor);
		default:
			throw new RuntimeException("Unknown Type " + descriptor.type.toString());
		}
	}

	public N createChild (N node, int level, int index, BoundingBox boundingBox) {
		if (node.childs == null || node.childs[index] == null) {
			if (node.childs == null) {
				node.childs = nodeProvider.createArray(level - 1, 8);
			}
			node.childs[index] = nodeProvider.create(level - 1);
			node.childs[index].parent = node;
			node.childs[index].index = (byte) index;
			node.childs[index].boundingBox.set(boundingBox);
			node.childs[index].childs = nodeProvider.createArray(level - 1, 8);
			return node.childs[index];
		}
		return node.childs[index];
	}

	private boolean deleteChild (N node) {
		if (node.parent == null) return false;
		N parent = node.parent;
		for (int i = 0; i < 8; ++i) {
			if (parent.childs == null) continue;
			if (parent.childs[i] == null) continue;
			if (parent.childs[i] != node) continue;
			parent.childs[i] = null;
			return true;
		}
		return false;
	}

	private void updateNode (N node, D descriptor) {
		if (node == null) return;
		while (node != null) {
			node.update(node, descriptor);
			node = node.parent;
		}
	}

	public N next (Vector3 v, N currentNode, D descriptor, BoundingBox boundingBox) {
		for (int index = 0; index < 8; index++) {
			if (currentNode.childs == null) continue;
			if (currentNode.childs[index] != null) {
				if (currentNode.childs[index].boundingBox.contains(v)) {
					return currentNode.childs[index];
				}
			}
		}
		return null;
	}
}
