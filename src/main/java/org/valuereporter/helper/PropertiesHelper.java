package org.valuereporter.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.Main;
import org.valuereporter.ValuereporterException;
import org.valuereporter.ValuereporterTechnicalException;

import java.io.*;
import java.util.Properties;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class PropertiesHelper {
    private static final Logger log = LoggerFactory.getLogger(PropertiesHelper.class);

    /**
     * Attempts to find and open a properties file from the filesystem or classpath.
     *
     * @param filename the name of the file to find
     * @return a Reader for the properties file, or null if not found
     */
    protected static Reader findPropertiesFile(String filename) {
        // First try to load from filesystem
        try {
            FileReader fileReader = new FileReader(filename);
            log.debug("Found properties file {} in filesystem", filename);
            return fileReader;
        } catch (Exception e) {
            log.debug("Could not find properties file in filesystem: {}, reason: {}", filename, e.getMessage());
        }

        // If not found in filesystem, try to load from classpath
        try {
            InputStream inputStream = PropertiesHelper.class.getClassLoader().getResourceAsStream(filename);
            if (inputStream != null) {
                log.debug("Found properties file {} in classpath", filename);
                return new InputStreamReader(inputStream);
            }
        } catch (Exception e) {
            log.debug("Could not find properties file in classpath: {}, reason: {}", filename, e.getMessage());
        }

        log.warn("Could not find properties file: {} in filesystem or classpath", filename);
        return null;
    }

    public static Properties findProperties() {
        Properties properties = new Properties();
        String classpathFileName = "valuereporter.properties";
        String overrideFileName = "./config_override/valuereporter.properties";
        Reader classpathFile = findPropertiesFile(classpathFileName);
        Reader overrideFile = findPropertiesFile(overrideFileName);

        if (overrideFile == null && classpathFile == null) {
            throw new ValuereporterException("Failed to load properties. Neither " + classpathFileName + " nor " + overrideFileName + " were found.", StatusType.RETRY_NOT_POSSIBLE);
        }

        try {
            if (classpathFile != null) {
                log.info("Loading properties from {}", classpathFileName);
                properties.load(classpathFile);
                classpathFile.close();
            }
            if (overrideFile != null) {
                log.info("Loading properties from {}", overrideFileName);
                properties.load(overrideFile);
                overrideFile.close();
            }
            log.debug("Properties loaded: {}", properties.toString());
        } catch (IOException e) {
            throw new ValuereporterException("Could not load properties from file.", e, StatusType.RETRY_NOT_POSSIBLE);
        } finally {
            // Ensure resources are closed
            closeQuietly(classpathFile);
            closeQuietly(overrideFile);
        }

        return properties;
    }

    /**
     * Safely closes a reader without throwing exceptions
     */
    private static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                log.warn("Failed to close reader", e);
            }
        }
    }

    public static int findHttpPort(Properties resources) throws ValuereporterException {
        int retPort = -1;
        String httpPort = resources.getProperty("jetty.http.port");

        if (httpPort == null || httpPort.length() == 0) {
            log.info("jetty.http.port missing. Will use default port {}", Main.DEFAULT_PORT_NO);
            retPort = Main.DEFAULT_PORT_NO;
        } else {
            try {
                retPort = Integer.parseInt(httpPort);  // Using parseInt instead of the deprecated constructor
            } catch (NumberFormatException nfe) {
                log.error("Could not convert {} to int. No jetty port is set.", httpPort);
                throw new ValuereporterTechnicalException("Property 'jetty.http.port' with value " + httpPort + " could not be cast to int.", StatusType.RETRY_NOT_POSSIBLE);
            }
        }
        return retPort;
    }
}