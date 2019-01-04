package au.net.kizzie.sensors;

import au.net.kizzie.pi.Pi;

/**
 *
 * @author steve
 */
public class UltrasonicSensorReaderFactory {
    public static UltrasonicSensorReader create() throws GroveSensorException {
        if (Pi.onPi) 
            return new UltrasonicSensorReaderImpl();
        else 
            return new UltrasonicSensorReaderStub();
    }
}
