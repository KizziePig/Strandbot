package au.net.kizzie.pi;

import au.net.kizzie.common.ConfigOverrides;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
    private static final String CONFIG_RESOURCE_BUNDLE = "au.net.kizzie.pi.config";
	
    public static final String MOVEMENT_STEPPERS_MIN_DISTANCE_TO_OBJECT_FOR_MOVEMENT = "movementSteppers.minDistanceToObjectForMovement";
    public static final String MOVEMENT_STEPPERS_STEPS_PER_METRE = "movementSteppers.stepsPerMetre";
    public static final String MOVEMENT_STEPPERS_SLEEP_TIME_BETWEEN_REQUEST_CHECKS_IN_MILLIS = "movementSteppers.sleepTimeBetweenRequestChecksInMillis";
    public static final String MOVEMENT_STEPPERS_SLEEP_TIME_BETWEEN_MOTOR_MOVEMENTS_IN_MILLIS = "movementSteppers.sleepTimeBetweenMotorMovementsInMillis";
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

    private Config() {
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
	
    public static long getLongProperty(String key) {
        synchronized (config) {
            try {
                return Long.parseLong(overrides.getProperty(key,getConfig().getProperty(key)));
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.SEVERE,"ERROR: Config.getLongProperty: Unable to convert value '{0}' to long config for property '{1}' in resource bundle '{2}': NumberFormatException: {3}",new Object[] {getConfig().getProperty(key),key,CONFIG_RESOURCE_BUNDLE,ex});
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
