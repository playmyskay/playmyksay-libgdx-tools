package com.playmyskay.config;

import com.badlogic.gdx.Preferences;

/* The ConfigHandler class handles several config relevant operations. */
public class ConfigHandler {
	private static ConfigHandler configHandler = new ConfigHandler();

	private Preferences preferences;
	private ConfigCollector configCollection = new ConfigCollector();

	private ConfigHandler() {

	}

	public void setPreferences (Preferences preferences) {
		this.preferences = preferences;
	}

	public static ConfigHandler get () {
		return configHandler;
	}

	/**
	 * @param clazz
	 *            object of an implemented Config type
	 * @return the config object if this object does not exists it will be
	 *         created and initialized
	 */
	public <T extends Config<T>> T getConfig (Class<T> clazz) {
		T config = configCollection.getConfigInternal(clazz);
		if (config == null) {
			try {
				config = clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			configCollection.add(config);
			config.init(preferences);
		}
		return config;
	}

	/** adding a ConfigListener which will be updated instantly */
	public <T extends Config<T>> void addListener (Class<T> clazz, ConfigListener<T> listener) {
		T config = getConfig(clazz);
		config.addListener(listener);
		listener.update(config);
	}

	/** persists the current preference state */
	public void persist () {
		preferences.flush();
	}
}
