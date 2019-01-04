package au.net.kizzie.sensors;

/**
 *
 * @author steve
 */
public interface UltrasonicSensorReader {
    static final long MIN_DISTANCE_IN_CMS = 3l;
    static final long MAX_DISTANCE_IN_CMS = 350l;
    /**
     * Take a reading 
     * @return  The distance in cms to the nearest object or null if none found
     * @throws au.net.kizzie.sensors.GroveSensorException 
     */
    public Long takeReading() throws GroveSensorException;
}
