package com.playmyskay.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;

public class WindowTools {
	public static void setFullscreen () {
		Monitor monitor = Gdx.graphics.getMonitor();
		DisplayMode displayMode = Gdx.graphics.getDisplayMode(monitor);
		if (!Gdx.graphics.setFullscreenMode(displayMode)) {
			// switching to full-screen mode failed
		}
	}

	public static void setSize (int width, int height) {
		Gdx.graphics.setWindowedMode(width, height);
	}

}
