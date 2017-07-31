package org.valuereporter.helper;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.Property;
import org.constretto.model.Resource;

import static org.valuereporter.helper.StringHelper.hasContent;

public class Configuration {
	
	private static final ConstrettoConfiguration configuration = new ConstrettoBuilder()
            .createPropertiesStore()
            .addResource(Resource.create("classpath:valuereporter.properties"))
            .addResource(Resource.create("file:./valuereporter_override.properties"))
			.addResource(Resource.create("file:./config_override/valuereporter.properties"))
            .done()
            .getConfiguration();
	
	private Configuration() {}
	
	public static String getString(String key) {
		return configuration.evaluateToString(key);
	}

	public static String getString(String key, String defaultValue) {
		String value = configuration.evaluateToString(key);
		if(hasContent(value)) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	public static Integer getInt(String key) {
		return configuration.evaluateToInt(key);
	}

	public static Integer getInt(String key, int defaultValue) {
		return configuration.evaluateTo(key, defaultValue);
	}

	public static boolean getBoolean(String key) {
		return configuration.evaluateToBoolean(key);
	}

	public static String printProperties() {
		String out = "Properties used are: \n";
		for (Property property : configuration) {
			out += "\t" + property.getKey() + ": " + property.getValue();
		}
		return out;
	}


}