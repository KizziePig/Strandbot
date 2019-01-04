package au.net.kizzie.test.grovepi;
import au.net.kizzie.sensors.GroveSensorBase;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.iot.raspberry.grovepi.GroveDigitalOut;

import org.iot.raspberry.grovepi.GrovePi;
import org.iot.raspberry.grovepi.devices.GroveLightSensor;
import org.iot.raspberry.grovepi.devices.GroveRgbLcd;
import org.iot.raspberry.grovepi.devices.GroveRotarySensor;
import org.iot.raspberry.grovepi.devices.GroveRotaryValue;
import org.iot.raspberry.grovepi.devices.GroveSoundSensor;
import org.iot.raspberry.grovepi.devices.GroveTemperatureAndHumiditySensor;
import org.iot.raspberry.grovepi.devices.GroveUltrasonicRanger;
import org.iot.raspberry.grovepi.pi4j.GrovePi4J;

public class ExerciseSensorsTestProgram {
    private final static Logger LOGGER = Logger.getLogger(ExerciseSensorsTestProgram.class.getName());
    
    /**
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Logger.getLogger("GrovePi").setLevel(Level.WARNING);
        GrovePi grovePi = new GrovePi4J();
        GroveDigitalOut led = grovePi.getDigitalOut(GroveSensorBase.BLUE_LED_DIGITAL_PIN);
        GroveDigitalOut buzzer = grovePi.getDigitalOut(GroveSensorBase.BUZZER_DIGITAL_PIN);
        GroveUltrasonicRanger ranger = new GroveUltrasonicRanger(grovePi, GroveSensorBase.ULTRASONIC_RANGER_DIGITAL_PIN);
        GroveTemperatureAndHumiditySensor dht = new GroveTemperatureAndHumiditySensor(grovePi, GroveSensorBase.TEMPERATURE_AND_HUMIDITY_SENSOR_DIGITAL_PIN, GroveTemperatureAndHumiditySensor.Type.DHT11);
        GroveLightSensor lightSensor = new GroveLightSensor(grovePi, GroveSensorBase.LIGHT_SENSOR_ANALOG_PIN);
        GroveRotarySensor rotarySensor = new GroveRotarySensor(grovePi, GroveSensorBase.ROTARY_SENSOR_ANALOG_PIN);
        GroveSoundSensor soundSensor = new GroveSoundSensor(grovePi, GroveSensorBase.SOUND_SENSOR_ANALOG_PIN);
        GroveDigitalOut redLed = grovePi.getDigitalOut(GroveSensorBase.RED_LED_DIGITAL_PIN);
        GroveDigitalOut greenLed = grovePi.getDigitalOut(GroveSensorBase.GREEN_LED_DIGITAL_PIN);
        GroveDigitalOut blueLed = grovePi.getDigitalOut(GroveSensorBase.BLUE_LED_DIGITAL_PIN);
        GroveRgbLcd lcd = grovePi.getLCD();

        lcd.setRGB(255,0,0);
        Thread.sleep(100);
        lcd.setText("Kizzie the pig!\nHello campers!");
        
        blueLed.set(false);
        greenLed.set(false);
        redLed.set(false);
        GroveDigitalOut onLed = null;
        while (true) {
          try {
            double distance = ranger.get();
            // log.log(Level.INFO, "Distance: {0}", distance);
            System.out.println("distance "+distance+" "+dht.get()+" light "+lightSensor.get()+" sound "+soundSensor.get());
            buzzer.set(distance < 20);
            //led.set(distance < 30);
            GroveRotaryValue value = rotarySensor.get();
            double degrees = value.getDegrees();
            if (degrees < 100.0 && onLed != blueLed) {
                System.out.println("Degrees: "+degrees+" blue");
                onLed = blueLed;
                blueLed.set(true);
                greenLed.set(false);
                redLed.set(false);
            } else if (degrees >= 100.0 && degrees < 250.0 && onLed != greenLed) {
                System.out.println("Degrees: "+degrees+" green");
                onLed = greenLed;
                blueLed.set(false);
                greenLed.set(true);
                redLed.set(false);
            } else if (degrees >= 250.0 && onLed != redLed) {
                System.out.println("Degrees: "+degrees+" red");
                onLed = redLed;
                blueLed.set(false);
                greenLed.set(false);
                redLed.set(true);
            }
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "main: Exception: {0}", ex);
          }
        }
    }
}
