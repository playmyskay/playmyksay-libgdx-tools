package com.playmyskay.voxel.actions.common;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.playmyskay.log.ILogger;
import com.playmyskay.octree.traversal.IntersectionData;
import com.playmyskay.octree.traversal.OctreeTraversalSettings;
import com.playmyskay.voxel.common.VoxelOctree;
import com.playmyskay.voxel.level.VoxelLevel;

public class ActionData {
	private VoxelOctree octree;
	private Ray ray;
	private ILogger logger;
	private List<VoxelLevel> nodeList;
	private List<Vector3> pointList;
	private OctreeTraversalSettings settings;
	private List<IntersectionData<VoxelLevel>> intersectionDataList;

	public VoxelOctree octree () {
		return octree;
	}

	public void clear () {
		octree = null;
		ray = null;
		logger = null;
		if (nodeList != null) nodeList.clear();
		if (pointList != null) pointList.clear();
		if (intersectionDataList != null) intersectionDataList.clear();
	}

	public void octree (VoxelOctree octree) {
		this.octree = octree;
	}

	public Ray ray () {
		return ray;
	}

	public void ray (Ray ray) {
		this.ray = ray;
	}

	public ILogger logger () {
		return logger;
	}

	public void logger (ILogger logger) {
		this.logger = logger;
	}

	public List<VoxelLevel> nodeList () {
		if (nodeList == null) nodeList = new ArrayList<>();
		return nodeList;
	}

	public List<Vector3> pointList () {
		if (pointList == null) pointList = new ArrayList<>();
		return pointList;
	}

	public List<IntersectionData<VoxelLevel>> intersectionDataList () {
		if (intersectionDataList == null) intersectionDataList = new ArrayList<>();
		return intersectionDataList;
	}

	public List<IntersectionData<VoxelLevel>> intersectionDataList (boolean create) {
		if (create) return intersectionDataList();
		return intersectionDataList;
	}

	public OctreeTraversalSettings settings () {
		if (settings == null) settings = new OctreeTraversalSettings();
		return settings;
	}

}
