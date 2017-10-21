package org.jpm.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Ashok Das
 * @since 21-10-2017
 * @see <a href="mailto:jackwenttohill@yahoo.co.uk?Subject=Hello%20World">Hello World</a>
 */
public class PropertyReader {
	
	Properties prop = new Properties();
	
	public PropertyReader() {
		
		try {
			 prop.load(this.getClass().getClassLoader().getResourceAsStream("PM.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getValue(String key) {
		
		return prop.getProperty(key);
	}
}