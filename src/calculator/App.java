package calculator;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import gui.window.WindowLogo;
import logginig.AbstractLogger.LogLevel;
import logginig.Logger;
import logginig.PrintStreamLogger;
import tools.Config;

public class App{
	public static Logger logger = Logger.getLogger(App.class);
	public static int COORDINATE_PRECISION = 6;
	public static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	
	public static final boolean SHOW_POINT_TEXT = true;
	
	public static Config config = new Config();
	
	public static void main(String[] args) throws IOException{	
		String logFileName = "log.txt";
		File logFile = new File(logFileName);
		if(logFile.exists()) logFile.delete();
		logFile = new File(logFileName);	

		Logger.setLogLevel(LogLevel.INFO.getMask() | LogLevel.DEBUG.getMask());
		Logger.subscribe(new PrintStreamLogger(System.out));
		Logger.subscribe(new PrintStreamLogger(new PrintStream(logFile)));
		
		/*ClassLoader cl = App.class.getClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();

        logger.info("Class-path:");
        for(URL url: urls){
        	String relative = "error while resolving path";
			try {
				relative = new File(".").toURI().relativize(url.toURI()).getPath();
			} catch (URISyntaxException e) {
				//am I bad person?
			}	
        	logger.info("\t" + relative);
        }*/
        
        App.config = new Config("app.properties");
		
		new WindowLogo();					
	}

}
