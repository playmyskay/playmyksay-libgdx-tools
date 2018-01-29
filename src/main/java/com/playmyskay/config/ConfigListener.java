package com.playmyskay.config;

/**
 * Interface to listen to config update events.
 * 
 * @author playmyskay
 */
public interface ConfigListener<T extends Config<T>> {
	public void update (T config);
}
