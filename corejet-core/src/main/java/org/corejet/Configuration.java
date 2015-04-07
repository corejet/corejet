package org.corejet;

import java.io.IOException;
import java.util.Properties;

import com.google.common.base.Strings;
import com.google.common.io.Resources;

public class Configuration {

	private static Configuration instance;
	private Properties properties;
	
	public static String BASE_REPORT_PREFIX = "/test-output/corejet-requirements-BASE-";

	private Configuration() {
		properties = new Properties();
		try {
			properties.load(Resources.getResource("corejet.properties").openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getProperty(String string) {
		String property = getInstance().properties.getProperty(string);
		
		if (property==null) {
			throw new RuntimeException("Property " + string + " could not be found!");
		}
		
		return property+"";
	}
	
	public static String getPropertyOrDefault(String propertyName, String defaultValue) {
		String value = null;
		try {
			value = getProperty(propertyName);
		} catch (RuntimeException e) {
			// value remains null
		}
		return Strings.isNullOrEmpty(value) ? defaultValue : value;
	}
	
	private static synchronized Configuration getInstance() {
		if (instance==null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	public static String getBaseDirectory(){
		return getPropertyOrDefault("corejet.report.directory", "target/corejet");
	}

}
