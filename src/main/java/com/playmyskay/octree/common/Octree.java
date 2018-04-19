package com.playmyskay.octree.common;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
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

	private Vector3 min = new Vector3();
	private Vector3 max = new Vector3();

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

	public synchronized N setNode (Vector3 v, D descriptor) {
		OctreeTools.adjustVector(v);

		if (!expandRootNode(v, descriptor)) {
			return null;
		}

		N updateNode = processDescriptor(v, descriptor, new OctreeCalc());
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

	private synchronized N processDescriptor (Vector3 v, D descriptor, OctreeCalc calc) {
		switch (descriptor.getBaseActionType()) {
		case add:
			return OctreeNodeTools.addNodeByVector(nodeProvider, rootNode, v, descriptor, calc);
		case remove:
			return removeNode(v, descriptor, calc);
		default:
			throw new RuntimeException("Unknown Type " + descriptor.getBaseActionType().toString());
		}
	}

	public N addNode (N node, D descriptor, OctreeCalc calc) {
		calc.boundingBox().set(node.boundingBox());
		if (descriptor.getBaseActionType() == BaseActionType.add) {
			Vector3 corner = new Vector3();
			expandRootNode(calc.boundingBox().getCorner000(corner), descriptor);
			expandRootNode(calc.boundingBox().getCorner100(corner), descriptor);
			expandRootNode(calc.boundingBox().getCorner001(corner), descriptor);
			expandRootNode(calc.boundingBox().getCorner101(corner), descriptor);
			expandRootNode(calc.boundingBox().getCorner010(corner), descriptor);
			expandRootNode(calc.boundingBox().getCorner110(corner), descriptor);
			expandRootNode(calc.boundingBox().getCorner011(corner), descriptor);
			expandRootNode(calc.boundingBox().getCorner111(corner), descriptor);

			return OctreeNodeTools.addNodeByBoundingBox(this, node, descriptor, calc);
		}

		return null;
	}

	public N removeNode (Vector3 v, D descriptor, OctreeCalc calc) {
		OctreeTools.adjustVector(v);

		N lastNode = null;
		N currentNode = rootNode;
		for (int level = curLevel; level > 0 && currentNode != null; --level) {
			lastNode = currentNode;
			currentNode = OctreeTools.contains(currentNode, v, calc);
		}

		if (currentNode == null && lastNode != null) {
			currentNode = lastNode;
		}

		lastNode = currentNode;

		OctreeTools.removeNode(currentNode);
		return lastNode;
	}

}
