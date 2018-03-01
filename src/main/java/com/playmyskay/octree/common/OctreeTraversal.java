package com.playmyskay.octree.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

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

	public static <N extends OctreeNode<N>> N get (Octree<N, ?> octree, N node, Vector3 v) {
		N rootNode = octree.rootNode;
		if (!rootNode.boundingBox().contains(v)) {
			return null;
		}

		for (int level = octree.curLevel; level >= node.depth(); --level) {
			node = next(node, v);
			if (node == null) {
				return null;
			}
		}

		return node;
	}

	public static class IntersectionData<N extends OctreeNode<N>> {
		public N node;
		public Vector3 point;
		public Vector3 normal;
	}

	public static class IntersectionRecorder<N extends OctreeNode<N>> {
		public int maxLevel = 0;
		public TreeSet<Integer> recordLevelSet = new TreeSet<>();
		public List<IntersectionData<N>> intersections = new ArrayList<>();
	}

	public static <N extends OctreeNode<N>> void intersects (N node, Ray ray, int level, IntersectionRecorder<N> ir) {
		if (node == null) return;
		if (ir.maxLevel == level) return;
		for (int i = 0; i < 8; i++) {
			if (node.childs() == null) continue;
			if (node.child(i) == null) continue;
			if (Intersector.intersectRayBoundsFast(ray, node.child(i).boundingBox())) {
				if (ir.recordLevelSet.contains(level - 1)) {
					Vector3 point = new Vector3();
					if (Intersector.intersectRayBounds(ray, node.child(i).boundingBox(), point)) {
						IntersectionData<N> entry = new IntersectionData<>();
						entry.node = node.child(i);
						entry.point = point;
						ir.intersections.add(entry);
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

		if (ir.recordLevelSet.contains(level)) {
			IntersectionData<N> entry = new IntersectionData<>();
			entry.node = node;
			ir.intersections.add(entry);
		}

		if (ir.maxLevel > level) return;
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
			Integer[] levels) {
		if (!Intersector.intersectRayBoundsFast(ray, octree.rootNode.boundingBox())) {
			return null;
		}

		IntersectionRecorder<N> ir = new IntersectionRecorder<N>();
		ir.recordLevelSet.addAll(Arrays.asList(levels));
		intersects(octree.rootNode, ray, octree.curLevel, ir);

		return ir;
	}

	public static <N extends OctreeNode<N>> IntersectionRecorder<N> getIntersections (Octree<N, ?> octree, Ray ray) {
		return getIntersections(octree, ray, new Integer[] { 0 });
	}

	public static <N extends OctreeNode<N>> IntersectionRecorder<N> getIntersections (Octree<N, ?> octree,
			BoundingBox boundingBox, Integer[] levels, int maxLevel) {
		if (!boundingBox.intersects(octree.rootNode.boundingBox())
				&& !boundingBox.contains(octree.rootNode.boundingBox())) {
			return null;
		}

		IntersectionRecorder<N> ir = new IntersectionRecorder<N>();
		ir.recordLevelSet.addAll(Arrays.asList(levels));
		ir.maxLevel = maxLevel;
		intersects(octree.rootNode, boundingBox, octree.curLevel, ir);

		return ir;
	}

	public static <N extends OctreeNode<N>> IntersectionRecorder<N> getIntersections (Octree<N, ?> octree,
			BoundingBox boundingBox) {
		return getIntersections(octree, boundingBox, new Integer[] { 0 }, 0);
	}

	public static <N extends OctreeNode<N>> IntersectionData<N> getNearestIntersection (Octree<N, ?> octree, Ray ray) {
		IntersectionRecorder<N> ir = getIntersections(octree, ray);
		if (ir != null && ir.intersections.size() > 0) {
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

	public static final Vector3[] normals = new Vector3[] { new Vector3(1f, 0f, 0f), new Vector3(-1f, 0f, 0f),
			new Vector3(0f, 1f, 0f), new Vector3(0f, -1f, 0f), new Vector3(0f, 0f, 1f), new Vector3(0f, 0f, -1f) };

	public static <N extends OctreeNode<N>> IntersectionData<N> getIntersectedNormal (Octree<N, ?> octree, Ray ray) {
		IntersectionData<N> entry = OctreeTraversal.getNearestIntersection(octree, ray);
		if (entry == null) return null;

		BoundingBox boundindBox = entry.node.boundingBox();
		Vector3 cnt = boundindBox.getCenter(new Vector3());

		// Build direction Vector via difference
		Vector3 direction = new Vector3().set(entry.point).sub(cnt);

		// find the intersection side of the bounding box
		int bestIndex = -1;
		float bestDot = 0f;
		for (int index = 0; index < normals.length; ++index) {
			Vector3 normal = normals[index];
			float dot = direction.dot(normal);
			if (dot >= 0 && dot > bestDot) {
				bestDot = dot;
				bestIndex = index;
			}
		}

		entry.normal = normals[bestIndex];
		return entry;
	}

}
