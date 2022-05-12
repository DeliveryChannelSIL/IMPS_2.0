package com.sil.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class WebServiceProperties {
	private static Properties properties = new Properties();

	/**
	 * This method used to read the wsErrorsMessages.properties file.
	 * 
	 * @author Sudarshan Maheshwari
	 * @date Dec 22, 2016
	 * @version $Revision$
	 * @param property
	 * @return
	 */
	public static String getErrorMsg(String property) {
		if (properties.containsKey(property.trim())) {
			return (String) properties.get(property);
		} else {
			ResourceBundle labels = ResourceBundle.getBundle("ini\\wsErrorsMessages");
			convertResourceBundleToProperties(labels);
			return (String) properties.get(property);
		}
	}

	/**
	 * 
	 * @author Sudarshan Maheshwari
	 * @date Dec 22, 2016
	 * @version $Revision$
	 * @param resource
	 */
	private static void convertResourceBundleToProperties(ResourceBundle resource) {
		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.put(key, resource.getString(key));
		}
	}

	/**
	 * This method used to read the wsErrorsCodes.properties file.
	 * 
	 * @author Sudarshan Maheshwari
	 * @date Dec 22, 2016
	 * @version $Revision$
	 * @param property
	 * @return
	 */
	public static String getErrorCode(String property) {
		if (properties.containsKey(property.trim())) {
			return (String) properties.get(property);
		} else {
			ResourceBundle labels = ResourceBundle.getBundle("ini\\wsErrorsCodes");
			convertResourceBundleToProperties(labels);
			return (String) properties.get(property);
		}
	}
}
