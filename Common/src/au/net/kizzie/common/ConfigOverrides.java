package au.net.kizzie.common;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Overrides are handled by this base class.
 * YOU MUST call initConfig before other calls
 */
public final class ConfigOverrides
{
    private static final Logger LOGGER = Logger.getLogger(ConfigOverrides.class.getName());
    private final Properties config;
    private ResourceBundle rb = null;
    private String overridesResourceBundle = null;

    /**
     * Use this within synchronized blocks
     * @param resourceBundle
     * @overrides pass in the fully qualified name of the resource bundle to override. Overrides is  
     */
    protected void initConfig(String resourceBundle) {
        if (config.isEmpty()) {
            this.overridesResourceBundle = resourceBundle+"Overrides";
            try {
                rb = ResourceBundle.getBundle(overridesResourceBundle);
                for (Enumeration<String> keys = rb.getKeys (); keys.hasMoreElements ();)  {
                    String key = (String) keys.nextElement ();
                    String value = rb.getString (key);
                    config.put (key, value);
                } 
            }  catch (Throwable th) {
                System.err.println("ERROR: ConfigOverrides.getConfig: Unable to load resource bundle '"+overridesResourceBundle+"': Throwable: "+th);
                th.printStackTrace(System.err);
            }
        }
    }

    public ConfigOverrides(String overridesResourceBundle)  {
        this.config = new Properties();
        initConfig(overridesResourceBundle);
    }

    public void reloadConfig() {
        config.clear();
        ResourceBundle.clearCache();
    }

    public String getProperty(String key, String defaultValue) {
        synchronized (config) {
            String overrideValue = config.getProperty(key);
            String result;
            if (overrideValue != null) {
                    result = overrideValue;
            }
            else {
                    result = defaultValue;
            }
            return result;	
        }
    }
}
