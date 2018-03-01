package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.common.OctreeNodeDescriptor.BaseActionType;

public class Octree<N extends OctreeNode<N>, D extends OctreeNodeDescriptor> {
	public int curLevel = -1;
	public int minDepth = 5;
	public N rootNode;
	public IOctreeNodeProvider<N> nodeProvider;
	private Vector3[] corners = new Vector3[8];
	private float[] dst2 = new float[8];
	private Vector3 tmp = new Vector3();

	public Octree(IOctreeNodeProvider<N> nodeProvider) {
		this.nodeProvider = nodeProvider;

		for (int i = 0; i < 8; i++) {
			corners[i] = new Vector3();
		}
	}

	private void createRootNode (Vector3 v) {
		if (rootNode != null) return;
		curLevel = minDepth - 1;
		rootNode = nodeProvider.create(curLevel);
		int dim = (int) Math.pow(2, curLevel);
		rootNode.boundingBox().set(new Vector3(0f, 0f, 0f), new Vector3(dim, dim, dim));
	}

	public N setNode (Vector3 v, D descriptor) {
		if (rootNode == null) createRootNode(v);
		if (!rootNode.boundingBox().contains(v)) {
			if (descriptor.getBaseActionType() == BaseActionType.remove) {
				return null;
			}

			Vector3 min = new Vector3();
			Vector3 max = new Vector3();
			while (!rootNode.boundingBox().contains(v)) {
				int near = OctreeTools.getNearestIndex(rootNode.boundingBox(), v, corners, dst2);
				int far = 7 - near;

				// min is the farthest corner of vector v
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
				newRootNode.boundingBox().set(min, max);

				// The new enclosing root node sets the current root node as its child.
				// The index is visually the nearest node of its origin expansion direction.
				newRootNode.childs(nodeProvider.createArray(curLevel, 8));
				newRootNode.child(far, rootNode);
				rootNode.parent(newRootNode);
				rootNode = newRootNode;

				curLevel++;

				System.out.println(String.format("rootNode: level(%d) index(%d)", curLevel, far));
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
			currentNode = OctreeTools.contains(v, currentNode);
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
		for (int level = curLevel; level > 0 && currentNode != null; --level) {
			lastNode = currentNode;
			currentNode = OctreeTools.contains(v, currentNode);
		}

		if (currentNode == null && lastNode != null) {
			currentNode = lastNode;
		}

		lastNode = currentNode;

		while (currentNode != null && currentNode.parent() != null) {
			deleteChild(currentNode);
			if (currentNode.parent().hasChilds()) break;

			currentNode = currentNode.parent();
		}

		return lastNode;
	}

	private N processDescriptor (Vector3 v, D descriptor) {
		switch (descriptor.getBaseActionType()) {
		case add:
			return addNode(v, descriptor);
		case remove:
			return removeNode(v, descriptor);
		default:
			throw new RuntimeException("Unknown Type " + descriptor.getBaseActionType().toString());
		}
	}

	public N createChild (N node, int level, int index, BoundingBox boundingBox) {
		if (node.childs() == null || node.child(index) == null) {
			if (node.childs() == null) {
				node.childs(nodeProvider.createArray(level - 1, 8));
			}
			node.child(index, nodeProvider.create(level - 1));
			node.child(index).parent(node);
			node.child(index).boundingBox().set(boundingBox);
			node.child(index).childs(nodeProvider.createArray(level - 1, 8));
			return node.child(index);
		}
		return node.child(index);
	}

	private boolean deleteChild (N node) {
		if (node.parent() == null) return false;
		N parent = node.parent();
		for (int i = 0; i < 8; ++i) {
			if (parent.childs() == null) continue;
			if (parent.child(i) == null) continue;
			if (parent.child(i) != node) continue;
			parent.child(i, null);
			return true;
		}
		return false;
	}

	private void updateNode (N updateNode, OctreeNodeDescriptor descriptor) {
		if (updateNode == null) return;
		N node = updateNode;
		while (node != null) {
			node.update(updateNode, descriptor);
			node = node.parent();
		}
	}

}
