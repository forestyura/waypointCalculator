package tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import calculator.App;
import logginig.Logger;

public class Config extends Properties {

	public static Logger logger = Logger.getLogger(App.class);
	
	public static String APP_ICON_PATH = "img/icon64.png";
	public static String APP_BLANK_MAP = "img/blank.png";
	public static String APP_LOGO_IMAGE = "img/logo.jpg";
	public static String APP_EXPORT_DIR = "export";
	public static String APP_SCRIPT_DIR = "scripts";

	public Config() {
		super();
	}
	
	public Config(String path) throws IOException {
		InputStream input = App.class.getClassLoader().getResourceAsStream(path);
        if(input != null) {
            this.load(input);
        } else {
        	logger.info("No property file found");
        }
	}

	public String getString(String key, String default_) {
		return getProperty(key, default_);		
	}
	
	public int getInt(String key, String default_) {
		return Integer.valueOf(getProperty(key, default_));
	}
	
	public boolean getBoolean(String key, String default_) {
		return Boolean.valueOf(getProperty(key, default_));
	}

}
