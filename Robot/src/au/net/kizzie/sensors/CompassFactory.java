package au.net.kizzie.sensors;

import au.net.kizzie.pi.Pi;

/**
 *
 * @author steve
 */
public class CompassFactory {
    public static Compass create() throws GroveSensorException {
        if (Pi.onPi) 
            return new CompassImpl();
        else 
            return new CompassStub();
    }
}
