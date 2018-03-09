package com.playmyskay.octree.common;

public interface IOctreeListener<N extends OctreeNode<N>, D extends OctreeNodeDescriptor> {

	public static class NodeUpdateData<N extends OctreeNode<N>, D extends OctreeNodeDescriptor> {
		public N node;
		public D descriptor;
	}

	public void update (NodeUpdateData<N, D> nodeUpdateData);
}
