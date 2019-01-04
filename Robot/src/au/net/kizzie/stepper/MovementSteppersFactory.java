package au.net.kizzie.stepper;

import au.net.kizzie.sensors.*;
import au.net.kizzie.pi.Pi;
/**
 *
 * @author steve
 */
public class MovementSteppersFactory {
    public static MovementSteppers create() throws GroveSensorException {
        if (Pi.onPi) 
            return new MovementSteppersImpl();
        else 
            return new MovementSteppersStub();
    }
}
