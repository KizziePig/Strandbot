package au.net.kizzie.sensors;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iot.raspberry.grovepi.devices.GroveUltrasonicRanger;

/**
 * Read the sensor and store its value in the database
 * This should be called following every move. 
 * It should be called in a thread as it takes time to read the sensor and store the value in the map db
 * @author steve
 */
public class UltrasonicSensorReaderImpl extends GroveSensorBase implements UltrasonicSensorReader  {
    private final static Logger LOGGER = Logger.getLogger(UltrasonicSensorReaderImpl.class.getName());


    private final GroveUltrasonicRanger ranger;
    
    /**
     * Do not call directly
     * Use the UltrasonicSensorReaderFactory to get an instance of this class
     * @throws au.net.kizzie.sensors.GroveSensorException
     */
    public UltrasonicSensorReaderImpl() throws GroveSensorException {
        super();
        ranger = new GroveUltrasonicRanger(grovePi, ULTRASONIC_RANGER_DIGITAL_PIN);
    }
    
    /**
     * Take a reading 
     * @return  The distance in cms to the nearest object or null if none found
     * @throws au.net.kizzie.sensors.GroveSensorException 
     */
    @Override
    public Long takeReading() throws GroveSensorException {
        try {
            long distance = Math.round(ranger.get());
            if (distance >= MIN_DISTANCE_IN_CMS && distance <= MAX_DISTANCE_IN_CMS) {
                return distance;
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "takeReading: Caught IOException trying to read the distance: {0} ", ex.getMessage());
            throw new GroveSensorException("takeReading: Unable to take the reading: IOException caught: "+ex.getMessage(),ex);
        }
        return null;
    }
}
