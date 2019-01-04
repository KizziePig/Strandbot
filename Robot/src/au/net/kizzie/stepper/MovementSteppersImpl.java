package au.net.kizzie.stepper;

import au.net.kizzie.pi.Config;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author steve
 */
public class MovementSteppersImpl extends Thread implements MovementSteppers {
    private static final Logger LOGGER = Logger.getLogger(MovementSteppersImpl.class.getName());

    class Movement {
        long stepsToMove;
        long stepsMoved;
        Boolean moving;
        Boolean emergencyStop;

        Movement() {
            this.emergencyStop = false;
            this.moving = false;
            this.stepsMoved = 0l;
            this.stepsToMove = 0l;
        }
    }
    // Lock the movement object before modifying 
    private final Movement movement = new Movement();
    
    private final GpioController gpio = GpioFactory.getInstance();
    private final GpioPinDigitalOutput[] pinsLeft;
    private final GpioPinDigitalOutput[] pinsRight;

    public MovementSteppersImpl() {
        // provision gpio pins as output pins and ensure in LOW state
        this.pinsLeft = new GpioPinDigitalOutput[] {
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26, PinState.LOW), // Yellow Dir
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, PinState.LOW), // Orange Step
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, PinState.LOW), // Red M0
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, PinState.HIGH)}; // Brown M1

        this.pinsRight = new GpioPinDigitalOutput[]  {
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.LOW), // Yellow Dir
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.LOW), // Orange Step
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW),  // Red M0
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.HIGH)}; // Brown M1
        
        gpio.setShutdownOptions(true, PinState.LOW, pinsLeft);
    	gpio.setState(PinState.LOW, pinsLeft[0]); // Yellow Dir
    	gpio.setState(PinState.LOW, pinsLeft[1]); // Orange Step
    	gpio.setState(PinState.LOW, pinsLeft[2]); // Red M0
    	gpio.setState(PinState.HIGH, pinsLeft[3]); // Brown M1
    	
        gpio.setShutdownOptions(true, PinState.LOW, pinsRight);
    	gpio.setState(PinState.LOW, pinsRight[0]); // Yellow Dir
    	gpio.setState(PinState.LOW, pinsRight[1]); // Orange Step
    	gpio.setState(PinState.LOW, pinsRight[2]); // Red M0
    	gpio.setState(PinState.HIGH, pinsRight[3]); // Brown M1

    }
    private int calcStepsFromDegrees(double degrees) {
        return (int) (degrees/1.8);                 // Each step is 1.8 degrees
    }
        /**
     * @param degrees  Pass in the number of degrees to turn to the right
     */
    private void turnRight(double degrees) {
        if (degrees < 0.0) {
            turnLeft(-degrees);
        } else {
            gpio.setState(PinState.LOW, pinsLeft[0]);
            gpio.setState(PinState.HIGH, pinsRight[0]);
            int steps = calcStepsFromDegrees(degrees);
            moveMotors(steps);
        }
    }
    /**
     * @param degrees  Pass in the number of degrees to turn to the right
     */
    private void turnLeft(double degrees) {
        if (degrees < 0.0) {
            turnRight(-degrees);
        } else {
            gpio.setState(PinState.HIGH, pinsLeft[0]);
            gpio.setState(PinState.LOW, pinsRight[0]);
            int steps = calcStepsFromDegrees(degrees);
            moveMotors(steps);
        }
    }
    private void forward(long steps) {
        gpio.setState(PinState.LOW, pinsLeft[0]);
        gpio.setState(PinState.LOW, pinsRight[0]);
        moveMotors(steps);
    }
    private void backward(long steps) {
        gpio.setState(PinState.HIGH, pinsLeft[0]);
        gpio.setState(PinState.HIGH, pinsRight[0]);
        moveMotors(steps);
    }
    private void moveMotors(long stepsToMove) {
        // Set up the movement and return
        synchronized(movement) {
            if (movement.moving) {
                LOGGER.log(Level.WARNING,"MovementSteppersImpl.move: Called but the robot is already moving. Always make sure it is not moving before calling move! - Ignoring request to move");
                return;
            }
            movement.stepsToMove = stepsToMove;
            movement.stepsMoved = 0l;
            movement.moving = true;
            movement.emergencyStop = false;
        }
    }

    /**
     * @param degrees Pass in the number of degrees to turn. Positive => turning right; negative => turning left
     */
    @Override
    public void turn(double degrees) {
        if (degrees < 0.0) {
            turnLeft(-degrees);
        } else {
            turnRight(degrees);
        }
    }
    @Override
    public void move(long distanceInCms) {
        long stepsToMove = Math.round((double) distanceInCms * 1000.0 / (double) Config.getLongProperty(Config.MOVEMENT_STEPPERS_STEPS_PER_METRE));
        if (distanceInCms < 0l)
            backward(stepsToMove);
        else
            forward(stepsToMove);
    }

    @Override
    public boolean isMoving() {
        synchronized(movement) {
            return movement.moving;
        }
    }

    /**
     * @return  The distance moved in cms
     */
    @Override
    public long distanceMoved() {
        synchronized(movement) {
            return Math.round((double) movement.stepsMoved * (double) Config.getLongProperty(Config.MOVEMENT_STEPPERS_STEPS_PER_METRE) / 1000.0);
        }
    }
    @Override 
    public void emergencyStop() {
        synchronized(movement) {
            movement.emergencyStop = true;
        }
    }
    
    /**
     * This function executes in the 
     * Continually loop and move the stepper motors when required
     */
    @Override
    public void run() {
        long motorSleepTime = Config.getIntProperty(Config.MOVEMENT_STEPPERS_SLEEP_TIME_BETWEEN_MOTOR_MOVEMENTS_IN_MILLIS);
        while(true) {   // This outer loop looks for an incoming request to move
            boolean needToMove = false;
            long stepsToMove = 0l;
            synchronized(movement) {
                if (movement.moving) {  // Someone has set this to true => need to move
                    needToMove = true;
                    stepsToMove = movement.stepsToMove;
                }
            }
            if (needToMove) {
                for (long i = 0; i < stepsToMove; i++) {
                    synchronized(movement) {
                        if (movement.emergencyStop)
                            break;
                    }
                    gpio.setState(PinState.HIGH, pinsLeft[1]);
                    gpio.setState(PinState.HIGH, pinsRight[1]);
                    synchronized(movement) {
                        movement.stepsMoved++;
                    }
                    try {
                        Thread.sleep(motorSleepTime);
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING,"MovementSteppersImpl.run: Interrupted while sleeping between motor movements - stopping motors!");
                        break;
                    }
                    gpio.setState(PinState.LOW, pinsLeft[1]);
                    gpio.setState(PinState.LOW, pinsRight[1]);
                }
                synchronized(movement) {
                    movement.moving = false;
                }
            }
            try {
                Thread.sleep(Config.getIntProperty(Config.MOVEMENT_STEPPERS_SLEEP_TIME_BETWEEN_REQUEST_CHECKS_IN_MILLIS));
            } catch (InterruptedException ex) {
                LOGGER.log(Level.WARNING,"MovementSteppersImpl.run: Interrupted while sleeping between request checks!");
            }
        }
    }

}
