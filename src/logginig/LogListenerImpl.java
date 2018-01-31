package logginig;

import logginig.AbstractLogger.LogLevel;

public abstract class LogListenerImpl implements LogListener {
	@Override
	public void update(LogLevel level, Class<?> clazz, String message) {
		if ((level.getMask() & Logger.getLogLevel()) > 0)
		{
			if(message == null) return;
			String source = (clazz == null) ? String.format("") : clazz.getSimpleName();
			
			if(message.length() > 1 && "\n".equals(message.substring(0, 1))){
				message = message.substring(1, message.length());
				appendText(String.format("%1$-25s", source));
			}
			appendText(String.format("%1$-5s%2$-25s",level, source) + message + "\n");
		}			
	}
	
	public abstract void appendText(String messaage);
	
}
