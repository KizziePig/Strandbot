package au.net.kizzie.sensors;

import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iot.raspberry.grovepi.GrovePi;
import org.iot.raspberry.grovepi.pi4j.GrovePi4J;

/**
 *
 * @author steve
 */
public class GroveSensorBase {
    private final static Logger LOGGER = Logger.getLogger(GroveSensorBase.class.getName());
    
    public final static int ROTARY_SENSOR_ANALOG_PIN = 0;
    public final static int SOUND_SENSOR_ANALOG_PIN = 1;
    public final static int LIGHT_SENSOR_ANALOG_PIN = 2;
    public final static int BLUE_LED_DIGITAL_PIN = 2;
    public final static int GREEN_LED_DIGITAL_PIN = 7;
    public final static int RED_LED_DIGITAL_PIN = 8;
    public final static int BUZZER_DIGITAL_PIN = 3;
    public final static int ULTRASONIC_RANGER_DIGITAL_PIN = 4;
    public final static int TEMPERATURE_AND_HUMIDITY_SENSOR_DIGITAL_PIN = 5;
    public final static int RELAY_DIGITAL_PIN = 6;

    protected GrovePi grovePi;
    
    public GroveSensorBase() throws GroveSensorException {
        try {
            grovePi = new GrovePi4J();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "GroveSensorBase(): Caught IOException trying to create the GrovePi4J instance: {0} ", ex.getMessage());
            throw new GroveSensorException("GroveSensorBase(): Unable to create the GrovePi4J instance: IOException caught: "+ex.getMessage(),ex);
        } catch(I2CFactory.UnsupportedBusNumberException ex) {
            LOGGER.log(Level.SEVERE, "GroveSensorBase(): Caught UnsupportedBusNumberException trying to create the GrovePi4J instance: {0} ", ex.getMessage());
            throw new GroveSensorException("GroveSensorBase(): Unable to create the GrovePi4J instance: UnsupportedBusNumberException caught: "+ex.getMessage(),ex);
        }
    }
}
