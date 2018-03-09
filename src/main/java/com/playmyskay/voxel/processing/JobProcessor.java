package com.playmyskay.voxel.processing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobProcessor {
	private static ExecutorService service = Executors.newFixedThreadPool(8);

	public static void add (Runnable runnable) {
		service.submit(runnable);
	}
}
