package au.net.kizzie.sensors;

import java.util.logging.Logger;

/**
 * Read the sensor and store its value in the database
 * This should be called following every move. 
 * It should be called in a thread as it takes time to read the sensor and store the value in the map db
 * @author steve
 */
public class CompassImpl extends GroveSensorBase implements Compass  {
    private final static Logger LOGGER = Logger.getLogger(CompassImpl.class.getName());
    
    /**
     * Do not call directly
     * Use the CompassFactory to get an instance of this class
     * @throws au.net.kizzie.sensors.GroveSensorException
     */
    public CompassImpl() throws GroveSensorException {
        super();
    }
    
     /**
     * Take a reading and return it.
     * @return The reading in degrees true or null if the compass can't get a decent reading
     * @throws au.net.kizzie.sensors.GroveSensorException 
     */
    @Override
    public Double takeReading() throws GroveSensorException {
        return null;
    }
}
