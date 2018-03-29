package com.playmyskay.octree.traversal;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.playmyskay.octree.common.Octree;
import com.playmyskay.octree.common.OctreeNode;

public class OctreeTraversal {

	public static <N extends OctreeNode<N>> N next (N node, Vector3 v) {
		if (!node.boundingBox().contains(v)) {
			return null;
		}

		for (int i = 0; i < 8; i++) {
			if (node.child(i) == null) continue;
			if (node.child(i).contains(v)) {
				return node.child(i);
			}
		}

		return null;
	}

	public static <N extends OctreeNode<N>> N get (Octree<N, ?> octree, Vector3 v) {
		N node = octree.rootNode;
		for (int level = octree.curLevel; level > 0; --level) {
			node = next(node, v);
			if (node == null) {
				return null;
			}
		}

		return node;
	}

	public static <N extends OctreeNode<N>> void intersects (N node, Ray ray, int level, IntersectionRecorder<N> ir) {
		if (node == null) return;
		if (ir.settings.maxLevel == level) return;
		for (int i = 0; i < 8; i++) {
			if (node.childs() == null) continue;
			if (node.child(i) == null) continue;
			if (Intersector.intersectRayBoundsFast(ray, node.child(i).boundingBox())) {
				if (ir.settings().recordLevelSet.contains(level - 1)) {
					Vector3 point = new Vector3();
					if (Intersector.intersectRayBounds(ray, node.child(i).boundingBox(), point)) {
						if (!ir.settings().filter(node.child(i))) {
							IntersectionData<N> id = new IntersectionData<>();
							id.node = node.child(i);
							id.point = point;
							ir.intersections.add(id);
						}
					}
				} else {
					intersects(node.child(i), ray, level - 1, ir);
				}
			}
		}
	}

	public static <N extends OctreeNode<N>> void intersects (N node, BoundingBox boundingBox, int level,
			IntersectionRecorder<N> ir) {
		if (node == null) return;

		if (ir.settings().recordLevelSet.contains(level)) {
			IntersectionData<N> entry = new IntersectionData<>();
			entry.node = node;
			ir.intersections.add(entry);
		}

		if (ir.settings.maxLevel > level) return;
		for (int i = 0; i < 8; i++) {
			if (node.childs() == null) continue;
			if (node.child(i) == null) continue;
			if (boundingBox.contains(node.child(i).boundingBox())
					|| boundingBox.intersects(node.child(i).boundingBox())) {
				intersects(node.child(i), boundingBox, level - 1, ir);
			}
		}
	}

	public static <N extends OctreeNode<N>> IntersectionRecorder<N> getIntersections (Octree<N, ?> octree, Ray ray,
			OctreeTraversalSettings settings) {
		if (octree == null || octree.rootNode == null) return null;
		if (!Intersector.intersectRayBoundsFast(ray, octree.rootNode.boundingBox())) {
			return null;
		}

		IntersectionRecorder<N> ir = new IntersectionRecorder<N>();
		ir.settings(settings);
		intersects(octree.rootNode, ray, octree.curLevel, ir);

		return ir;
	}

	public static <N extends OctreeNode<N>> IntersectionRecorder<N> getIntersections (Octree<N, ?> octree, Ray ray) {
		OctreeTraversalSettings settings = new OctreeTraversalSettings();
		settings.recordLevelSet.add(0);
		return getIntersections(octree, ray, settings);
	}

	public static <N extends OctreeNode<N>> IntersectionRecorder<N> getIntersections (Octree<N, ?> octree,
			BoundingBox boundingBox, OctreeTraversalSettings settings) {
		if (octree == null || octree.rootNode == null) return null;
		if (!boundingBox.intersects(octree.rootNode.boundingBox())
				&& !boundingBox.contains(octree.rootNode.boundingBox())) {
			return null;
		}

		IntersectionRecorder<N> ir = new IntersectionRecorder<N>();
		ir.settings(settings);
		intersects(octree.rootNode, boundingBox, octree.curLevel, ir);

		return ir;
	}

	public static <N extends OctreeNode<N>> IntersectionRecorder<N> getIntersections (Octree<N, ?> octree,
			BoundingBox boundingBox) {
		OctreeTraversalSettings settings = new OctreeTraversalSettings();
		settings.recordLevelSet.add(0);
		return getIntersections(octree, boundingBox, settings);
	}

	public static <N extends OctreeNode<N>> IntersectionData<N> getClosestIntersection (Octree<N, ?> octree, Ray ray,
			OctreeTraversalSettings settings) {
		IntersectionRecorder<N> ir = getIntersections(octree, ray, settings);
		if (ir != null && !ir.intersections.isEmpty()) {
			if (ir.intersections.size() == 1) {
				return ir.intersections.get(0);
			}

			IntersectionData<N> nearestEntry = null;
			float distance = 0f;
			float nearestDistance = Float.POSITIVE_INFINITY;
			for (IntersectionData<N> entry : ir.intersections) {
				distance = entry.point.dst2(ray.origin);
				if (distance < nearestDistance) {
					nearestEntry = entry;
					nearestDistance = distance;
				}
			}

			return nearestEntry;
		}
		return null;
	}

	public static <N extends OctreeNode<N>> IntersectionData<N> getIntersectedNormal (Octree<N, ?> octree, Ray ray,
			OctreeTraversalSettings settings) {
		IntersectionData<N> entry = OctreeTraversal.getClosestIntersection(octree, ray, settings);
		if (entry == null) return null;
		return entry;
	}

}
