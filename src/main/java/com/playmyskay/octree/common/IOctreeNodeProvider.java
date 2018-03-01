package com.playmyskay.octree.common;

public interface IOctreeNodeProvider<N extends OctreeNode<N>> {
	public N create (int level);

	public N[] createArray (int level, int size);

	public int depth (Class<?> clazz);
}
