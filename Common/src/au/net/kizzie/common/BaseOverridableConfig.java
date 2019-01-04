package au.net.kizzie.common;

/**
 *
 * @author steve
 */

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseOverridableConfig {
    private static final Logger LOGGER = Logger.getLogger(BaseOverridableConfig.class.getName());

    private final Properties config;
    private ResourceBundle rb = null;
    private final ConfigOverrides overrides = new ConfigOverrides(getConfigResourceBundleName());

    public BaseOverridableConfig() {
        this.config = new Properties();
    }

    /**
     * Implement this to return the name of the config resource bundle e.g. "au.com.ezy2c.location.config"
     * @return The bundle name
     */
    protected abstract String getConfigResourceBundleName();


    /**
     * Use this within synchronized blocks
     */
    private Properties getConfig() {
        if (config.isEmpty())  {
            try {
                rb = ResourceBundle.getBundle(getConfigResourceBundleName());
                for (Enumeration<String> keys = rb.getKeys (); keys.hasMoreElements ();) {
                    String key = (String) keys.nextElement ();
                    String value = rb.getString (key);
                    config.put (key, value);
                } 
            }  catch (Throwable th) {
                    System.err.println("ERROR: BaseOverridableConfig.getConfig: Unable to load resource bundle '"+getConfigResourceBundleName()+"': Throwable: "+th);
                    th.printStackTrace(System.err);
            }
        }
        return config;
    }

    public void reloadConfig() {
        config.clear();
        ResourceBundle.clearCache();
    }

    public String getProperty(String key) {
        synchronized (config) {
            return overrides.getProperty(key,getConfig().getProperty(key));
        }
    }


    public int getIntProperty(String key) {
        synchronized (config) {
            try {
                return Integer.parseInt(overrides.getProperty(key,getConfig().getProperty(key)));
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.SEVERE,"ERROR: BaseOverridableConfig.getIntProperty: Unable to convert value '{0}' to integer config for property '{1}' in resource bundle '{2}': NumberFormatException: {3}",new Object[] {overrides.getProperty(key,getConfig().getProperty(key)),key,getConfigResourceBundleName(),ex});
                throw ex;
            }
        }
    }

    public long getLongProperty(String key) {
        synchronized (config) {
            try {
                return Long.parseLong(overrides.getProperty(key,getConfig().getProperty(key)));
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.SEVERE,"ERROR: BaseOverridableConfig.getLongProperty: Unable to convert value '{0}' to lonog config for property '{1}' in resource bundle '{2}': NumberFormatException: {3}",new Object[] {overrides.getProperty(key,getConfig().getProperty(key)),key,getConfigResourceBundleName(),ex});
                throw ex;
            }
        }
    }

    public float getFloatProperty(String key) {
        synchronized (config) {
            try {
                return Float.parseFloat(overrides.getProperty(key,getConfig().getProperty(key)));
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.SEVERE,"ERROR: BaseOverridableConfig.getFloatProperty: Unable to convert value '{0}' to float config for property '{1}' in resource bundle '{2}': NumberFormatException: {3}",new Object[] {overrides.getProperty(key,getConfig().getProperty(key)),key,getConfigResourceBundleName(),ex});
                throw ex;
            }
        }
    }

    public Boolean getBooleanProperty(String key) {
        synchronized (config) {
            return Boolean.parseBoolean(overrides.getProperty(key,getConfig().getProperty(key)));
        }
    }

    public Timestamp getTimestampProperty(String key) throws ParseException {
        synchronized (config) {
            try {
                String dateString = overrides.getProperty(key,getConfig().getProperty(key));
                if (dateString == null || dateString.trim().equals(""))
                    return null;
                return DateUtil.convertStringToTimestamp(dateString);
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.SEVERE,"ERROR: BaseOverridableConfig.getTimestampProperty: Unable to convert value '{0}' to timestamp config for property '{1}' in resource bundle '{2}': NumberFormatException: {3}",new Object[] {overrides.getProperty(key,getConfig().getProperty(key)),key,getConfigResourceBundleName(),ex});
                throw ex;
            } catch (ParseException ex) {
                LOGGER.log(Level.SEVERE,"ERROR: BaseOverridableConfig.getTimestampProperty: Unable to convert value '{0}' to timestamp config for property '{1}' in resource bundle '{2}': ParseException: {3}",new Object[] {overrides.getProperty(key,getConfig().getProperty(key)),key,getConfigResourceBundleName(),ex});
                throw ex;
            }
        }
    }
}

