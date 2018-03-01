package com.playmyskay.log;

public class Logger implements ILogger {

	private static ILogger logger = null;

	public static void setLogger (ILogger logger) {
		Logger.logger = logger;
	}

	public static ILogger get () {
		return logger;
	}

	@Override
	public void log (Object val) {
		if (logger != null) {
			logger.log(val);
		}
	}

}
