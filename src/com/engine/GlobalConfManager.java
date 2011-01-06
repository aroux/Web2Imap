package com.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class GlobalConfManager {
	
	private static final Logger logger = Logger.getLogger(GlobalConfManager.class);
	
	private static GlobalConfManager self = null;
	
	private Map<String, String> properties;
	
	private GlobalConfManager() {
		properties = new HashMap<String, String>();
		Properties props = new Properties();
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			//URL propFileURL = new URL("classpath:global.properties");
			//props.load(new FileInputStream(new File(propFileURL.toURI())));
			props.load(classLoader.getResourceAsStream("global.properties"));
			for (Map.Entry<Object,Object> entry : props.entrySet()) {
				properties.put((String) entry.getKey(), (String) entry.getValue());
			}
		} catch (IOException e) {
			logger.fatal("Impossible to read global configuration. Exiting.", e);
			System.exit(-1);
		}
//		} catch (URISyntaxException e) {
//			logger.fatal("Impossible to read global configuration. Exiting.", e);
//			System.exit(-1);
//		}
	}
	
	public static GlobalConfManager getInstance() {
		if (self == null) self = new GlobalConfManager();
		return self;
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}
}
