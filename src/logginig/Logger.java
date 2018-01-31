package logginig;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger extends AbstractLogger{
	
	private Logger(Class<?> clazz){
		super(clazz);
	}
	
	public static Logger getLogger(Class<?> clazz){
		return new Logger(clazz);
	}
	
	@Override
	public void info(String message){
		for(LogListener ll : listeners){
			ll.update(LogLevel.INFO, clazz, message);
		}
	}
	
	@Override
	public void debug(String message){
		for(LogListener ll : listeners){
			ll.update(LogLevel.DEBUG, clazz, message);
		}
	}
	
	@Override
	public void trace(String message){
		for(LogListener ll : listeners){
			ll.update(LogLevel.TRACE, clazz, message);
		}
	}

	public void info(Exception e) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter( writer );
		e.printStackTrace( printWriter );
		printWriter.flush();
		
		info(writer.toString());
	}
	
}
