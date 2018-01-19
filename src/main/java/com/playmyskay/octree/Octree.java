package com.playmyskay.octree;

import com.badlogic.gdx.math.Vector3;

public class Octree {
	public int treeDepth = 1;
	public OctreeNode rootNode = new OctreeNode();
	private Vector3[] corners = new Vector3[8];
	private float[] dst2 = new float[8];

	public Octree() {
		rootNode.boundingBox.set(new Vector3(-0.5f, -0.5f, -0.5f), new Vector3(0.5f, 0.5f, 0.5f));

		for (int i = 0; i < 8; i++) {
			corners[i] = new Vector3();
		}
	}

	public void addNode (Vector3 v) {
		if (!rootNode.boundingBox.contains(v)) {
			Vector3 cnt = new Vector3();
			while (!rootNode.boundingBox.contains(v)) {
				rootNode.boundingBox.getCorner000(corners[0]);
				rootNode.boundingBox.getCorner001(corners[1]);
				rootNode.boundingBox.getCorner010(corners[2]);
				rootNode.boundingBox.getCorner011(corners[3]);
				rootNode.boundingBox.getCorner100(corners[4]);
				rootNode.boundingBox.getCorner101(corners[5]);
				rootNode.boundingBox.getCorner110(corners[6]);
				rootNode.boundingBox.getCorner111(corners[7]);

				dst2[0] = corners[0].dst2(v);
				dst2[1] = corners[1].dst2(v);
				dst2[2] = corners[2].dst2(v);
				dst2[3] = corners[3].dst2(v);
				dst2[4] = corners[4].dst2(v);
				dst2[5] = corners[5].dst2(v);
				dst2[6] = corners[6].dst2(v);
				dst2[7] = corners[7].dst2(v);

				int near = 0;
				int far = 0;
				for (int i = 1; i < 8; i++) {
					if (dst2[i] > dst2[far]) {
						far = i;
					} else if (dst2[i] < dst2[near]) {
						near = i;
					}
				}
				System.out.println(near);
				System.out.println(far);

				rootNode.boundingBox.getCenter(cnt);
				Vector3 min = new Vector3().set(corners[near]);
				Vector3 max = new Vector3().set(min).scl(2f);

				OctreeNode newRootNode = new OctreeNode();
				newRootNode.boundingBox.set(min, max);

				int index = Math.abs(7 - near);
				System.out.println(newRootNode.boundingBox.getCenter(new Vector3()));
				newRootNode.childs = new OctreeNode[8];
				newRootNode.childs[index] = rootNode;
				rootNode.parent = newRootNode;
				rootNode = newRootNode;

				treeDepth++;
			}
		}

	}
}
