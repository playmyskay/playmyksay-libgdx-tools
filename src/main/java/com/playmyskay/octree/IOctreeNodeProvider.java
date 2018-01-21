package com.playmyskay.octree;

public interface IOctreeNodeProvider<T extends OctreeNode<T, ?>> {
	public T create (int level);

	public T[] createArray (int level, int size);
}
