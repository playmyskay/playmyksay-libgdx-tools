package com.playmyskay.voxel.world;

public interface IChunkUpdateListener {
	public void add (UpdateData updateData);

	public UpdateData create ();
}
