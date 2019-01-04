package au.net.kizzie.sensors;

/**
 *
 * @author steve
 */
public interface Compass {
     /**
     * Take a reading and return it.
     * @return The reading in degrees true or null if the compass can't get a decent reading
     * @throws au.net.kizzie.sensors.GroveSensorException 
     */
    public Double takeReading() throws GroveSensorException;
}
