package au.net.kizzie.common;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommonConfig {
    private static final Logger LOGGER = Logger.getLogger(CommonConfig.class.getName());
    private static final String CONFIG_RESOURCE_BUNDLE = "au.net.kizzie.common.commonConfig";
	
    public static final String ROBOT_HOST = "robot.host";
    public static final String ROBOT_PORT = "robot.port";
    public static final String MAP_HOST = "map.host";
    public static final String MAP_PORT = "map.port";
    public static final String ROBOT_RESPONSE_INVALID_REQUEST = "robot.response.invalidRequest";
    public static final String ROBOT_RESPONSE_FAILURE_RESPONSE = "robot.response.failureResponse";
    public static final String ROBOT_RESPONSE_NO_OBJECT = "robot.response.noObject";
    
    public static final String ROBOT_REQUEST_ULTRASONIC_READ = "robot.request.ultrasonicRead";
    
    public static final String MAP_REQUEST_GET_MAP = "map.request.getMap";
    public static final String MAP_REQUEST_TURN = "map.request.turn";
    public static final String MAP_REQUEST_MOVE = "map.request.move";
    public static final String MAP_REQUEST_IS_CLEAR = "map.request.isClear";
    public static final String MAP_RESPONSE_FAILURE_RESPONSE = "map.response.failureResponse";
    
    public static void reloadConfig() {
            config.clear();
            ResourceBundle.clearCache();
    }
		
    private static final Properties config = new Properties();
    private static ResourceBundle rb = null;
    private static final ConfigOverrides overrides = new ConfigOverrides(CONFIG_RESOURCE_BUNDLE);
	
    private static Properties getConfig() {
        if (config.isEmpty()) {
            try {
                rb = ResourceBundle.getBundle (CONFIG_RESOURCE_BUNDLE);
                for (Enumeration<String> keys = rb.getKeys (); keys.hasMoreElements ();) {
                    String key = (String) keys.nextElement ();
                    String value = rb.getString (key);
                    System.out.println("Config.getConfig: found "+key+"="+value);
                    config.put (key, value);
                } 
            }  catch (Throwable th) {
                    System.err.println("ERROR: Config.getConfig: Unable to load property resource bundle '"+CONFIG_RESOURCE_BUNDLE+"': Throwable: "+th);
                    th.printStackTrace(System.err);
            }
        }
        return config;
    }

    private CommonConfig() {
    }

    public static String getProperty(String key) {
        synchronized (config) {
            String value = overrides.getProperty(key,getConfig().getProperty(key));
            //System.err.println("Config.getProperty: dir = '"+dir+"' key '"+key+"' value '"+value+"'");
            return value;
        }
    }
	
	
    public static int getIntProperty(String key) {
        synchronized (config) {
            try {
                return Integer.parseInt(overrides.getProperty(key,getConfig().getProperty(key)));
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.SEVERE,"ERROR: Config.getIntProperty: Unable to convert value '{0}' to integer config for property '{1}' in resource bundle '{2}': NumberFormatException: {3}",new Object[] {getConfig().getProperty(key),key,CONFIG_RESOURCE_BUNDLE,ex});
                throw ex;
            }
        }
    }
	
    public static Boolean getBooleanProperty(String key) {
        synchronized (config) {
            return Boolean.parseBoolean(overrides.getProperty(key,getConfig().getProperty(key)));
        }
    }
	
}
