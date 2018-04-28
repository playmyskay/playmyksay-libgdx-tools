package com.playmyskay.octree.common;

import java.util.concurrent.ConcurrentLinkedQueue;

public class OctreeCalcPoolManager {
	private static OctreeCalcPoolManager manager = new OctreeCalcPoolManager();

	private ConcurrentLinkedQueue<OctreeCalc> pool = new ConcurrentLinkedQueue<>();

	public static OctreeCalc obtain () {
		OctreeCalc calc = manager.pool.poll();
		if (calc == null) {
			calc = new OctreeCalc();
		}
		calc.reset();
		return calc;
	}

	public static void free (OctreeCalc calc) {
		manager.pool.offer(calc);
	}
}
