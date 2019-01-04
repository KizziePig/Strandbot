package au.net.kizzie.test;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * NOTE: The WiringPi pinouts are used not the BCM pinouts found in internet diagrams. 
 pi@raspberrypi:~ $ gpio readall
 +-----+-----+---------+------+---+---Pi 3---+---+------+---------+-----+-----+
 | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
 +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
 |     |     |    3.3v |      |   |  1 || 2  |   |      | 5v      |     |     |
 |   2 |   8 |   SDA.1 |   IN | 1 |  3 || 4  |   |      | 5v      |     |     |
 |   3 |   9 |   SCL.1 |   IN | 1 |  5 || 6  |   |      | 0v      |     |     |
 |   4 |   7 | GPIO. 7 |   IN | 1 |  7 || 8  | 0 | IN   | TxD     | 15  | 14  |
 |     |     |      0v |      |   |  9 || 10 | 1 | IN   | RxD     | 16  | 15  |
 |  17 |   0 | GPIO. 0 |   IN | 0 | 11 || 12 | 0 | ALT0 | GPIO. 1 | 1   | 18  |
 |  27 |   2 | GPIO. 2 |   IN | 0 | 13 || 14 |   |      | 0v      |     |     |
 |  22 |   3 | GPIO. 3 |   IN | 0 | 15 || 16 | 0 | IN   | GPIO. 4 | 4   | 23  |
 |     |     |    3.3v |      |   | 17 || 18 | 0 | IN   | GPIO. 5 | 5   | 24  |
 |  10 |  12 |    MOSI | ALT0 | 0 | 19 || 20 |   |      | 0v      |     |     |
 |   9 |  13 |    MISO | ALT0 | 0 | 21 || 22 | 0 | IN   | GPIO. 6 | 6   | 25  |
 |  11 |  14 |    SCLK | ALT0 | 0 | 23 || 24 | 1 | OUT  | CE0     | 10  | 8   |
 |     |     |      0v |      |   | 25 || 26 | 1 | OUT  | CE1     | 11  | 7   |
 |   0 |  30 |   SDA.0 |   IN | 1 | 27 || 28 | 1 | IN   | SCL.0   | 31  | 1   |
 |   5 |  21 | GPIO.21 |   IN | 1 | 29 || 30 |   |      | 0v      |     |     |
 |   6 |  22 | GPIO.22 |   IN | 1 | 31 || 32 | 0 | IN   | GPIO.26 | 26  | 12  |
 |  13 |  23 | GPIO.23 |   IN | 0 | 33 || 34 |   |      | 0v      |     |     |
 |  19 |  24 | GPIO.24 | ALT0 | 0 | 35 || 36 | 0 | IN   | GPIO.27 | 27  | 16  |
 |  26 |  25 | GPIO.25 |   IN | 0 | 37 || 38 | 0 | ALT0 | GPIO.28 | 28  | 20  |
 |     |     |      0v |      |   | 39 || 40 | 0 | ALT0 | GPIO.29 | 29  | 21  |
 +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
 | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
 +-----+-----+---------+------+---+---Pi 3---+---+------+---------+-----+-----+
 * 
 * 
 * For the DRV8880 wire +5 from phys pin 2 or 4 to the SLP line above the STEP and DIR lines and wire GND from phys pin 6 to the GND line below FLT on the motor outputs side
 * Also connect the motor main power to VM and GND and the 4 stepper motor wires red, blue, black, green to B1, B2, A2, A1 respectively
 */
public class StepperMotor {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Started");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pins as output pins and ensure in LOW state
	
       
        // Motor 1
        GpioPinDigitalOutput[] pins1 = {
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26, PinState.LOW), // Yellow Dir  - physical pin 32
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.LOW), // Orange Step - physical pin 36
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, PinState.LOW), // Red M0      - physical pin 38 - See Pololu page for DRV8880 Step size section
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, PinState.LOW)}; // Brown M1  - physical pin 40  - See Pololu page for DRV8880 Step size section
                
        // Motor 2
        GpioPinDigitalOutput[] pins2 = {
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.LOW), // Yellow Dir  - physical pin 31
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.LOW), // Orange Step - physical pin 33
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW),  // Red M0     - physical pin 35
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.HIGH)}; // Brown M1  - physical pin 37
	
        // Uncomment following line to test Motor 2 instead
        //pins1 = pins2;
        //
        // this will ensure that the motor is stopped when the program terminates
        gpio.setShutdownOptions(true, PinState.LOW, pins1);
    	gpio.setState(PinState.LOW, pins1[0]); // Yellow Dir
    	gpio.setState(PinState.LOW, pins1[1]); // Orange Step
    	gpio.setState(PinState.LOW, pins1[2]); // Red M0
    	gpio.setState(PinState.LOW, pins1[3]); // Brown M1
    	
        gpio.setShutdownOptions(true, PinState.LOW, pins2);
    	gpio.setState(PinState.LOW, pins2[0]); // Yellow Dir
    	gpio.setState(PinState.LOW, pins2[1]); // Orange Step
    	gpio.setState(PinState.LOW, pins2[2]); // Red M0
    	gpio.setState(PinState.LOW, pins2[3]); // Brown M1
    	
    	gpio.setState(PinState.LOW, pins1[2]);
    	gpio.setState(PinState.HIGH, pins1[3]);
        
   	gpio.setState(PinState.LOW, pins2[2]);
    	gpio.setState(PinState.HIGH, pins2[3]);
        
        System.out.println("Starting loop");
    	for (int i = 0; i < 500; i++) {
  //      while(true) {
    		gpio.setState(PinState.HIGH, pins1[1]);
    		gpio.setState(PinState.HIGH, pins2[1]);
    		Thread.sleep(10);
    		gpio.setState(PinState.LOW, pins1[1]);
    		gpio.setState(PinState.LOW, pins2[1]);
	    	Thread.sleep(10);
                //System.out.println("At end of loop");
	}
        System.out.println("Loop finished");
	/*
	//gpio.pulse(5000,pins[0]);
    Pin pin = RaspiPin.GPIO_01;
 	GpioPinPwmOutput pwm = gpio.provisionPwmOutputPin(pin);

        // you can optionally use these wiringPi methods to further customize the PWM generator
        // see: http://wiringpi.com/reference/raspberry-pi-specifics/
        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(500);

        // set the PWM rate to 500
        pwm.setPwm(500);
        System.out.println("PWM rate is: " + pwm.getPwm());

        System.out.println("Press ENTER to set the PWM to a rate of 250");
        Thread.sleep(10000);

        // set the PWM rate to 250
        pwm.setPwm(250);
        System.out.println("PWM rate is: " + pwm.getPwm());


        System.out.println("Press ENTER to set the PWM to a rate to 0 (stop PWM)");
        Thread.sleep(10000);

        // set the PWM rate to 0
        pwm.setPwm(0);
        System.out.println("PWM rate is: " + pwm.getPwm());
	
        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();
*/
       // System.out.println("Finished");
    }
}
