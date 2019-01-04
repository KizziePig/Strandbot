package au.net.kizzie.sensors;


/**
 * This is the test stub to be used when not on the Pi. @see au.org.kizzie.common.Pi
 * @author steve
 */
public class CompassStub implements Compass  {
    
    /**
     * Do not call directly
     * Use the CompassFactory to get an instance of this class
     * @param mapModel
     */
    public CompassStub() {
    }
    
     /**
     * Take a reading and return it.
     * @return The reading in degrees true or null if the compass can't get a decent reading
     */
    @Override
    public Double takeReading() {
        return null;
    }
}
