package com.playmyskay.voxel.render;

import java.util.List;

public class RenderChange {

	public enum Type {
		create, remove
	}

	public RenderChange.Type type;
	public List<RenderableItem> renderableItems;

	public RenderChange(RenderChange.Type type) {
		this.type = type;
	}

	public RenderChange setRenderableItems (List<RenderableItem> list) {
		this.renderableItems = list;
		return this;
	}
}