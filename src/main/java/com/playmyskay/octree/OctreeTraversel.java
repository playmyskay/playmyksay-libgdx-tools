package com.playmyskay.octree;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class OctreeTraversel {

	public static <N extends OctreeNode<N, ?>> N next (N node, Vector3 v) {
		if (!node.boundingBox.contains(v)) {
			return null;
		}

		for (int i = 0; i < 8; i++) {
			if (node.childs[i] == null) continue;
			if (node.childs[i].contains(v)) {
				return node.childs[i];
			}
		}

		return null;
	}

	public static <N extends OctreeNode<N, ?>> N get (Octree<N, ?> octree, N node, Vector3 v) {
		N rootNode = octree.rootNode;
		if (!rootNode.boundingBox.contains(v)) {
			return null;
		}

		for (int level = octree.curLevel; level >= node.getDepth(); --level) {
			node = next(node, v);
			if (node == null) {
				return null;
			}
		}

		return node;
	}

	public static class IntersectionEntry<N extends OctreeNode<N, ?>> {
		public N node;
		public Vector3 point;
		public Vector3 normal;
	}

	public static class IntersectionRecorder<N extends OctreeNode<N, ?>> {
		public int level = 0;
		public List<IntersectionEntry<N>> intersections = new ArrayList<>();
	}

	public static <N extends OctreeNode<N, ?>> void intersects (N node, Ray ray, int level,
			IntersectionRecorder<N> ir) {
		if (node == null) return;
		for (int i = 0; i < 8; i++) {
			if (node.childs == null) continue;
			if (node.childs[i] == null) continue;
			if (Intersector.intersectRayBoundsFast(ray, node.childs[i].boundingBox)) {
				if (ir.level == level - 1) {
					Vector3 point = new Vector3();
					if (Intersector.intersectRayBounds(ray, node.childs[i].boundingBox, point)) {
						IntersectionEntry<N> entry = new IntersectionEntry<>();
						entry.node = node.childs[i];
						entry.point = point;
						ir.intersections.add(entry);
					}
				} else {
					intersects(node.childs[i], ray, --level, ir);
				}
			}
		}
	}

	public static <N extends OctreeNode<N, ?>> IntersectionRecorder<N> getIntersections (Octree<N, ?> octree, Ray ray) {
		if (!Intersector.intersectRayBoundsFast(ray, octree.rootNode.boundingBox)) {
			return null;
		}

		IntersectionRecorder<N> ir = new IntersectionRecorder<N>();
		intersects(octree.rootNode, ray, octree.curLevel, ir);

		return ir;
	}

	public static <N extends OctreeNode<N, ?>> IntersectionEntry<N> getNearestIntersection (Octree<N, ?> octree,
			Ray ray) {
		IntersectionRecorder<N> ir = getIntersections(octree, ray);
		if (ir != null && ir.intersections.size() > 0) {
			if (ir.intersections.size() == 1) {
				return ir.intersections.get(0);
			}

			IntersectionEntry<N> nearestEntry = null;
			float distance = 0f;
			float nearestDistance = Float.MAX_VALUE;
			for (IntersectionEntry<N> entry : ir.intersections) {
				distance = entry.point.dst2(ray.origin);
				if (distance < nearestDistance) {
					nearestDistance = distance;
				}
			}

			return nearestEntry;
		}
		return null;
	}

	public static final Vector3[] normals = new Vector3[] { new Vector3(1f, 0f, 0f), new Vector3(-1f, 0f, 0f),
			new Vector3(0f, 1f, 0f), new Vector3(0f, -1f, 0f), new Vector3(0f, 0f, 1f), new Vector3(0f, 0f, -1f) };

	public static <N extends OctreeNode<N, ?>> IntersectionEntry<N> getIntersectedNormal (Ray ray,
			Octree<N, ?> octree) {
		IntersectionEntry<N> entry = OctreeTraversel.getNearestIntersection(octree, ray);
		if (entry == null) return null;

		BoundingBox boundindBox = entry.node.boundingBox;
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
