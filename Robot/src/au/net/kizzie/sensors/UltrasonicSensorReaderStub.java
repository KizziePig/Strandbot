package au.net.kizzie.sensors;

import java.util.logging.Logger;

/**
 * This is the test stub to be used when not on the Pi. @see au.org.kizzie.common.Pi
 * Read the sensor and store its value in the database
 * This should be called following every move. 
 * It should be called in a thread as it takes time to read the sensor and store the value in the map db
 * @author steve
 */
public class UltrasonicSensorReaderStub implements UltrasonicSensorReader  {
    private final static Logger LOGGER = Logger.getLogger(UltrasonicSensorReaderStub.class.getName());
    
    
    /**
     * Do not call directly
     * Use the UltrasonicSensorReaderFactory to get an instance of this class
     */
    public UltrasonicSensorReaderStub() {
    }
    
    /**
     * Take a reading 
     * @return  The distance in cms to the nearest object or null if none found
     * @throws au.net.kizzie.sensors.GroveSensorException 
     */
    @Override
    public Long takeReading() throws GroveSensorException {
        return null;       
    }
}
