package logginig;

import logginig.AbstractLogger.LogLevel;

public interface LogListener {
	public void update(LogLevel level, Class<?> clazz, String message);
}
