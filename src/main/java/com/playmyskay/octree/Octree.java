package com.playmyskay.octree;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.playmyskay.octree.OctreeNodeDescriptor.Type;

public class Octree<N extends OctreeNode<N, D>, D extends OctreeNodeDescriptor> {
	public int treeLevel = 1;
	public N rootNode;
	public IOctreeNodeProvider<N> nodeProvider;
	private Vector3[] corners = new Vector3[8];
	private float[] dst2 = new float[8];

	public Octree(IOctreeNodeProvider<N> nodeProvider) {
		this.nodeProvider = nodeProvider;
		rootNode = nodeProvider.create(0);
		rootNode.boundingBox.set(new Vector3(), new Vector3(1f, 1f, 1f));

		for (int i = 0; i < 8; i++) {
			corners[i] = new Vector3();
		}
	}

	public void setNode (Vector3 v, D descriptor) {
		if (!rootNode.boundingBox.contains(v)) {
			if (descriptor.type == Type.remove) {
				return;
			}

			Vector3 cnt = new Vector3();
			while (!rootNode.boundingBox.contains(v)) {
				corners[0] = getCorner(0, rootNode.boundingBox);
				corners[1] = getCorner(1, rootNode.boundingBox);
				corners[2] = getCorner(2, rootNode.boundingBox);
				corners[3] = getCorner(3, rootNode.boundingBox);
				corners[4] = getCorner(4, rootNode.boundingBox);
				corners[5] = getCorner(5, rootNode.boundingBox);
				corners[6] = getCorner(6, rootNode.boundingBox);
				corners[7] = getCorner(7, rootNode.boundingBox);

				dst2[0] = corners[0].dst2(v);
				dst2[1] = corners[1].dst2(v);
				dst2[2] = corners[2].dst2(v);
				dst2[3] = corners[3].dst2(v);
				dst2[4] = corners[4].dst2(v);
				dst2[5] = corners[5].dst2(v);
				dst2[6] = corners[6].dst2(v);
				dst2[7] = corners[7].dst2(v);

				int near = 0;
				int far = 0;
				for (int i = 1; i < 8; i++) {
					if (dst2[i] > dst2[far]) {
						far = i;
					} else if (dst2[i] < dst2[near]) {
						near = i;
					}
				}

				rootNode.boundingBox.getCenter(cnt);
				Vector3 min = new Vector3().set(corners[far]);
				Vector3 max = new Vector3().set(corners[near]).scl(2f);

				N newRootNode = nodeProvider.create(treeLevel + 1);
				newRootNode.boundingBox.set(min, max);

				// the new enclosing root node indexing the prior root node 
				// in the diagonally node/corner instead of its nearest point.
				// This is due to the fact the world should be expanded 
				int index = Math.abs(7 - near);
				newRootNode.childs = nodeProvider.createArray(treeLevel, 8);
				newRootNode.childs[index] = rootNode;
				rootNode.parent = newRootNode;
				rootNode.index = (byte) index;
				rootNode = newRootNode;

				treeLevel++;
			}
		}

		N currentNode = rootNode;
		if (descriptor.type == Type.add) {
			for (int level = treeLevel; level >= 0 && currentNode != null; --level) {
				currentNode = next(v, level, currentNode, descriptor, new BoundingBox());
			}
		} else if (descriptor.type == Type.remove) {
			N lastNode = rootNode;
			for (int level = treeLevel; level >= 0 && currentNode != null; --level) {
				lastNode = currentNode;
				currentNode = next(v, level, currentNode, descriptor, new BoundingBox());
			}

			if (currentNode == null) {
				currentNode = lastNode;
			}

			// clean up
			if (currentNode != null) {
				N parentNode = currentNode.parent;
				for (int i = 0; i < 7; ++i) {
					if (parentNode.childs[i] == currentNode) {
						parentNode.childs[i] = null;
						break;
					}
				}

				boolean empty = true;
				while (parentNode != null && empty) {
					for (int i = 0; i < 7 && empty; ++i) {
						if (parentNode.childs[i] != null) {
							empty = false;
						}
					}
					if (empty) {
						for (int i = 0; i < 7; ++i) {
							if (parentNode.childs[i] == currentNode) {
								parentNode.childs[i] = null;
								break;
							}
						}

						parentNode = parentNode.parent;
					}
				}
			}
		} else {
			throw new RuntimeException();
		}

		if (currentNode != null) {
			N parentNode = currentNode;
			while (parentNode != null) {
				parentNode.update(currentNode, descriptor);
				parentNode = parentNode.parent;
			}
		}

	}

	public void setNode (OctreePosition position) {

	}

	public void calculateChildBounds (int index, N parentNode, BoundingBox out) {
		// calculate bounding box
		out.set(getCorner(index, parentNode.boundingBox), parentNode.boundingBox.getCenter(new Vector3()));
	}

	public static Vector3 getCorner (int index, BoundingBox boundingBox) {
		Vector3 corner = new Vector3();
		switch (index) {
		case 0:
			return boundingBox.getCorner000(corner);
		case 1:
			return boundingBox.getCorner100(corner);
		case 2:
			return boundingBox.getCorner001(corner);
		case 3:
			return boundingBox.getCorner110(corner);
		case 4:
			return boundingBox.getCorner010(corner);
		case 5:
			return boundingBox.getCorner110(corner);
		case 6:
			return boundingBox.getCorner011(corner);
		case 7:
			return boundingBox.getCorner111(corner);
		}
		return null;
	}

	public N next (Vector3 v, int level, N currentNode, D descriptor, BoundingBox boundingBox) {
		for (int index = 0; index < 8; index++) {
			if (descriptor.type == Type.add) {
				if (currentNode.childs[index] == null) {
					calculateChildBounds(index, currentNode, boundingBox);
					if (boundingBox.contains(v)) {
						currentNode.childs[index] = nodeProvider.create(level);
						currentNode.childs[index].parent = currentNode;
						currentNode.childs[index].index = (byte) index;
						currentNode.childs[index].boundingBox.set(boundingBox);
						currentNode.childs[index].childs = nodeProvider.createArray(level, 8);
						return currentNode.childs[index];
					}
				} else {
					if (currentNode.childs[index].boundingBox.contains(v)) {
						return currentNode.childs[index];
					}
				}
			} else if (descriptor.type == Type.remove) {
				if (currentNode.childs[index] != null) {
					if (currentNode.childs[index].boundingBox.contains(v)) {
						return currentNode.childs[index];
					}
				}
			}
		}
		return null;
	}
}
