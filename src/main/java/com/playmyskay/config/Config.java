package com.playmyskay.config;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Preferences;

/**
 * Base class to implement specific config types.
 * 
 * @author playmyskay
 */
public abstract class Config<T extends Config<T>> {
	private Preferences preferences;
	private List<ConfigListener<T>> listeners = new ArrayList<ConfigListener<T>>();

	/* get this object of the implementation type */
	@SuppressWarnings("unchecked")
	protected T getConfig () {
		return (T) this;
	}

	/**
	 * initialization. fires instantly an update event.
	 */
	public void init (Preferences preferences) {
		setPreferences(preferences);
		updateEvent();
	}

	/** adding a ConfigListener */
	public void addListener (ConfigListener<T> listener) {
		listeners.add(listener);
	}

	/** calling update method of all listeners */
	public void updateEvent () {
		for (ConfigListener<T> listener : listeners) {
			listener.update(getConfig());
		}
	}

	public void setPreferences (Preferences preferences) {
		this.preferences = preferences;
	}

	public Preferences getPreferences () {
		return preferences;
	}
}
