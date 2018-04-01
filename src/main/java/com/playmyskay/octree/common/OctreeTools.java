package com.playmyskay.octree.common;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class OctreeTools {
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
			return boundingBox.getCorner101(corner);
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

	public static void getConers (Vector3[] corners, BoundingBox boundingBox) {
		corners[0] = getCorner(0, boundingBox);
		corners[1] = getCorner(1, boundingBox);
		corners[2] = getCorner(2, boundingBox);
		corners[3] = getCorner(3, boundingBox);
		corners[4] = getCorner(4, boundingBox);
		corners[5] = getCorner(5, boundingBox);
		corners[6] = getCorner(6, boundingBox);
		corners[7] = getCorner(7, boundingBox);
	}

	public static void getDistances (Vector3 v, Vector3[] corners, float[] dst2) {
		dst2[0] = corners[0].dst2(v);
		dst2[1] = corners[1].dst2(v);
		dst2[2] = corners[2].dst2(v);
		dst2[3] = corners[3].dst2(v);
		dst2[4] = corners[4].dst2(v);
		dst2[5] = corners[5].dst2(v);
		dst2[6] = corners[6].dst2(v);
		dst2[7] = corners[7].dst2(v);
	}

	public static int getNearestIndex (BoundingBox boundingBox, Vector3 v, Vector3[] corners, float[] dst2) {
		OctreeTools.getConers(corners, boundingBox);
		OctreeTools.getDistances(v, corners, dst2);

		int near = 0;
		for (int i = 1; i < 8; i++) {
			if (dst2[i] < dst2[near]) {
				near = i;
			}
		}
		return near;
	}

	public static <N extends OctreeNode<N>> void calculateBounds (int index, OctreeNode<N> parentNode,
			BoundingBox out) {
		Vector3 corner = OctreeTools.getCorner(index, parentNode.boundingBox());
		Vector3 cnt = parentNode.boundingBox().getCenter(new Vector3());
		out.set(corner, cnt);
	}

	public static void adjustVector (Vector3 v) {
		if (v.x % 1f == 0f || v.x == 0f) {
			v.x += 0.5f;
		}
		if (v.y % 1f == 0f || v.y == 0f) {
			v.y += 0.5f;
		}
		if (v.z % 1f == 0f || v.z == 0f) {
			v.z += 0.5f;
		}
	}

	public static <N extends OctreeNode<N>> N contains (N currentNode, Vector3 v) {
		for (int index = 0; index < 8; index++) {
			if (currentNode.childs() == null) continue;
			if (currentNode.child(index) != null) {
				if (currentNode.child(index).boundingBox().contains(v)) {
					return currentNode.child(index);
				}
			}
		}
		return null;
	}

	public static <N extends OctreeNode<N>> N contains (N currentNode, BoundingBox boundingBox) {
		for (int index = 0; index < 8; index++) {
			if (currentNode.childs() == null) continue;
			if (currentNode.child(index) != null) {
				if (currentNode.child(index).boundingBox().contains(boundingBox)) {
					return currentNode.child(index);
				}
			}
		}
		return null;
	}

	public static <N extends OctreeNode<N>> N createChild (IOctreeNodeProvider<N> nodeProvider, N node, int level,
			int index, BoundingBox boundingBox, OctreeNodeDescriptor descriptor) {
		if (node.childs() == null || node.child(index) == null) {
			if (node.childs() == null) {
				node.childs(nodeProvider.createArray(level - 1, 8));
			}
			node.child(index, nodeProvider.create(level - 1));
			node.child(index).parent(node);
			if (node.child(index).hasBoundingBox()) {
				node.child(index).boundingBox().set(boundingBox);
			}
			node.child(index).childs(nodeProvider.createArray(level - 1, 8));
			node.child(index).descriptor(descriptor);
			return node.child(index);
		}
		return node.child(index);
	}

	public static <N extends OctreeNode<N>> boolean deleteChild (N node) {
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

	public static <N extends OctreeNode<N>> void updateNode (N updateNode, OctreeNodeDescriptor descriptor) {
		if (updateNode == null) return;
		N node = updateNode;
		while (node != null) {
			node.update(updateNode, descriptor);
			node = node.parent();
		}
	}
}
