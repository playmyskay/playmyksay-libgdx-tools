package com.playmyskay.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to collect all config type objects.
 * 
 * @author playmyskay
 */

public class ConfigCollector {
	private List<Config<?>> configs = new ArrayList<Config<?>>();

	public <T extends Config<T>> T getConfigInternal (Class<T> clazz) {
		for (Config<?> config : configs) {
			if (config.getClass().isAssignableFrom(clazz)) {
				return clazz.cast(config);
			}
		}
		return null;
	}

	public void add (Config<?> config) {
		configs.add(config);
	}

	public void reset () {
		configs.clear();
	}
}
