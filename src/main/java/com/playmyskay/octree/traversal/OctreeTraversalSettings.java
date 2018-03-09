package com.playmyskay.octree.traversal;

import java.util.TreeSet;

public class OctreeTraversalSettings {
	public int maxLevel = 0;
	public TreeSet<Integer> recordLevelSet = new TreeSet<>();
	public IOctreeNodeFilter filter;

	public boolean filter (Object node) {
		if (filter == null) return false;
		return filter.filter(node);
	}
}
