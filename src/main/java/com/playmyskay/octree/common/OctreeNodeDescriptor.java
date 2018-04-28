package com.playmyskay.octree.common;

public class OctreeNodeDescriptor {
	public enum BaseActionType {
		add, remove
	}

	private BaseActionType baseActionType;

	public OctreeNodeDescriptor(BaseActionType baseActionType) {
		this.baseActionType = baseActionType;
	}

	public void setBaseActionType (BaseActionType baseActionType) {
		this.baseActionType = baseActionType;
	}

	public BaseActionType getBaseActionType () {
		return this.baseActionType;
	}
}
