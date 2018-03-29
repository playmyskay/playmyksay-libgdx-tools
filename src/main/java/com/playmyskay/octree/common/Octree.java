package com.playmyskay.octree.common;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.common.IOctreeListener.NodeUpdateData;
import com.playmyskay.octree.common.OctreeNodeDescriptor.BaseActionType;

public class Octree<N extends OctreeNode<N>, D extends OctreeNodeDescriptor> {
	public int curLevel = -1;
	public int minDepth = 5;
	public N rootNode;
	public IOctreeNodeProvider<N> nodeProvider;
	private Vector3[] corners = new Vector3[8];
	private float[] dst2 = new float[8];
	private Vector3 tmp = new Vector3();
	private List<IOctreeListener<N, D>> octreeListenerList = new ArrayList<>();

	public Octree() {
		for (int i = 0; i < 8; i++) {
			corners[i] = new Vector3();
		}
	}

	public void setNodeProvider (IOctreeNodeProvider<N> nodeProvider) {
		this.nodeProvider = nodeProvider;
	}

	public void addListener (IOctreeListener<N, D> octreeListener) {
		octreeListenerList.add(octreeListener);
	}

	public void removeListener (IOctreeListener<N, D> octreeListener) {
		octreeListenerList.remove(octreeListener);
	}

	private void createRootNode (Vector3 v) {
		if (rootNode != null) return;
		curLevel = minDepth - 1;
		rootNode = nodeProvider.create(curLevel);
		int dim = (int) Math.pow(2, curLevel);
		rootNode.boundingBox().set(new Vector3(0f, 0f, 0f), new Vector3(dim, dim, dim));
	}

	private boolean needRootExpansion (Vector3 v) {
		return !rootNode.boundingBox().contains(v);
	}

	private boolean expandRootNode (Vector3 v, D descriptor) {
		if (descriptor.getBaseActionType() == BaseActionType.remove) {
			return false;
		}

		if (rootNode == null) {
			createRootNode(v);
		}

		if (!needRootExpansion(v)) {
			return true;
		}

		Vector3 min = new Vector3();
		Vector3 max = new Vector3();
		while (needRootExpansion(v)) {
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
		}
		return true;
	}

	public N setNode (Vector3 v, D descriptor) {
		if (!expandRootNode(v, descriptor)) {
			return null;
		}

		N updateNode = processDescriptor(v, descriptor);
		OctreeTools.updateNode(updateNode, descriptor);

		updateListeners(updateNode, descriptor);

		return updateNode;
	}

	private void updateListeners (N updateNode, D descriptor) {
		NodeUpdateData<N, D> updateData = new NodeUpdateData<>();
		updateData.node = updateNode;
		updateData.descriptor = descriptor;
		for (IOctreeListener<N, D> listener : octreeListenerList) {
			listener.update(updateData);
		}
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

	private N addNode (Vector3 v, D descriptor) {
		OctreeTools.adjustVector(v);

		N parentNode = null;
		N childNode = rootNode;
		BoundingBox boundingBox = new BoundingBox();
		for (int level = curLevel; level > 0 && childNode != null; --level) {
			parentNode = childNode;
			childNode = OctreeTools.contains(childNode, v);
			if (childNode != null) continue;

			for (int index = 0; index < 8; index++) {
				OctreeTools.calculateBounds(index, parentNode, boundingBox);
				if (boundingBox.contains(v)) {
					childNode = OctreeTools.createChild(nodeProvider, parentNode, level, index, boundingBox);
				}
			}
		}
		return childNode;
	}

	private N addNode (N node, D descriptor) {
		BoundingBox boundingBox = new BoundingBox();
		boundingBox.set(node.boundingBox());

		N parentNode = null;
		N childNode = rootNode;
		for (int level = curLevel; level > 0 && childNode != null; --level) {
			parentNode = childNode;
			childNode = OctreeTools.contains(childNode, node.boundingBox());
			if (childNode != null) continue;

			for (int index = 0; index < 8; index++) {
				OctreeTools.calculateBounds(index, parentNode, boundingBox);
				if (boundingBox.contains(node.boundingBox())) {
					node.parent(parentNode);
					parentNode.child(index, node);
					return node;
				}
			}
		}
		return null;
	}

	public N removeNode (Vector3 v, D descriptor) {
		OctreeTools.adjustVector(v);

		N lastNode = null;
		N currentNode = rootNode;
		for (int level = curLevel; level > 0 && currentNode != null; --level) {
			lastNode = currentNode;
			currentNode = OctreeTools.contains(currentNode, v);
		}

		if (currentNode == null && lastNode != null) {
			currentNode = lastNode;
		}

		lastNode = currentNode;

		while (currentNode != null && currentNode.parent() != null) {
			OctreeTools.deleteChild(currentNode);
			if (currentNode.parent().hasChilds()) break;

			currentNode = currentNode.parent();
		}

		return lastNode;
	}

}
