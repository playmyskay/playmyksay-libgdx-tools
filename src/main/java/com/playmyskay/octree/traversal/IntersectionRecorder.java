package com.playmyskay.octree.traversal;

import java.util.ArrayList;
import java.util.List;

import com.playmyskay.octree.common.OctreeNode;

public class IntersectionRecorder<N extends OctreeNode<N>> {
	public OctreeTraversalSettings settings;
	public List<IntersectionData<N>> intersections = new ArrayList<>();

	public void settings (OctreeTraversalSettings settings) {
		this.settings = settings;
	}

	public OctreeTraversalSettings settings () {
		return settings;
	}
}