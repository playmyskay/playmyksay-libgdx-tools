package com.playmyskay.voxel.processing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JobProcessor {
	private static ExecutorService service = Executors.newFixedThreadPool(3);

	public static Future<?> add (Runnable runnable) {
		return service.submit(runnable);
	}
}
