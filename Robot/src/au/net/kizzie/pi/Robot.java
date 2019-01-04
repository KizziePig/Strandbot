package au.net.kizzie.pi;


import au.net.kizzie.common.CommonConfig;
import au.net.kizzie.sensors.Compass;
import au.net.kizzie.sensors.CompassFactory;
import au.net.kizzie.sensors.GroveSensorException;
import au.net.kizzie.sensors.UltrasonicSensorReader;
import au.net.kizzie.sensors.UltrasonicSensorReaderFactory;
import au.net.kizzie.stepper.MovementSteppers;
import au.net.kizzie.stepper.MovementSteppersFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author steve
 */
public class Robot {
    private static final Logger LOGGER = Logger.getLogger(Robot.class.getName());
    
    private static Robot robot = null;
    public static Robot getRobot() throws RobotException {
        if (robot == null) 
            robot = new Robot();
        return robot;
    }
    
    private MovementSteppers movementSteppers;
    private UltrasonicSensorReader ultrasonicSensorReader;
    private Compass compass;
    
    public Robot() throws RobotException {
        try {
            this.movementSteppers = MovementSteppersFactory.create();
        } catch (GroveSensorException ex) {
            LOGGER.log(Level.SEVERE, "Robot(): Unable to create the MovementSteppers: GroveSensorException {0}",ex.getMessage());
            throw new RobotException("Unable to create the MovementSteppers: GroveSensorException caught",ex);
        }
        try {
            this.ultrasonicSensorReader = UltrasonicSensorReaderFactory.create();
        } catch (GroveSensorException ex) {
            LOGGER.log(Level.SEVERE, "Robot(): Unable to create the UltrasonicSensorReader: GroveSensorException {0}",ex.getMessage());
            throw new RobotException("Unable to create the UltrasonicSensorReader: GroveSensorException caught",ex);
        }
        try {
            this.compass = CompassFactory.create();
        } catch (GroveSensorException ex) {
            LOGGER.log(Level.SEVERE, "Robot(): Unable to create the Compass: GroveSensorException {0}",ex.getMessage());
            throw new RobotException("Unable to create the Compass: GroveSensorException caught",ex);
        }
    }
    
    /**
     * Turns to the new direction and reads the ultrasonic ranger
     * @param direction The new direction in degrees true between 0.0 and 360.0
     * @return true if moved okay
     */
    public boolean turn(double direction) {
        // Get the current direction from the compass
        Double currentDirection;
        try {
            currentDirection = compass.takeReading();
            if (currentDirection == null) {
                LOGGER.log(Level.INFO, "Robot.turn: Unable to take compass reading - returning false");
                return false;
            }
        } catch (GroveSensorException ex) {
            LOGGER.log(Level.WARNING, "Robot.turn: Unable to take compass reading: Grove Sensor Exception caught: {0} - returning false",ex.getMessage());
            return false;
        }
        double deltaDirection = direction - currentDirection;
        LOGGER.log(Level.INFO, "Robot.turn: Turning from current direction {0} to new direction {1} i.e. {2} a degree turn", new Object[] {currentDirection, direction, deltaDirection});
        movementSteppers.turn(deltaDirection);
        
        LOGGER.log(Level.INFO, "Robot.turn: Finished turning from direction {0} to new direction {1}", new Object[] {currentDirection, direction});
        return true;
    }
    /**
     * Moves the robot reading the ultrasonic ranger in parallel and stopping the move if necessary
     * @param distance in cms to move
     * @return The distance moved 
     * @throws au.net.kizzie.pi.RobotException
     */
    public Long move(long distance) throws RobotException {
        // Check the ultrasonic reader for the latest information in the desired direction
        Long distanceToObject;
        int minDistanceRequired = Config.getIntProperty(Config.MOVEMENT_STEPPERS_MIN_DISTANCE_TO_OBJECT_FOR_MOVEMENT);
        try {
            distanceToObject = ultrasonicSensorReader.takeReading();
        } catch (GroveSensorException ex) {
           LOGGER.log(Level.SEVERE, "Robot.move: Unable to read the UltrasonicSensorReader: GroveSensorException {0}",ex.getMessage());
           throw new RobotException("Unable to read the UltrasonicSensorReader: GroveSensorException caught",ex);
        }
        if (distanceToObject > minDistanceRequired) {
           movementSteppers.move(distance);
        }
        while (movementSteppers.isMoving()) {
            try {
                distanceToObject = ultrasonicSensorReader.takeReading();
            } catch (GroveSensorException ex) {
               LOGGER.log(Level.SEVERE, "Robot.move: Unable to read the UltrasonicSensorReader: GroveSensorException {0}",ex.getMessage());
               throw new RobotException("Unable to read the UltrasonicSensorReader: GroveSensorException caught",ex);
            }
            if (distanceToObject <= minDistanceRequired) {
                LOGGER.log(Level.INFO, "Robot.move: Object too close {0}cms: stopping",new Object[] {distanceToObject});
                movementSteppers.emergencyStop();
            }
        }
        long distanceMoved = movementSteppers.distanceMoved();
        LOGGER.log(Level.INFO, "Robot.move: Finished. Requested distance {0}, moved distance {1}",new Object[] {distance, distanceMoved});
        return distanceMoved;
    }
    /**
     * 
     * @return returns null if unable to read it or the distance to the nearest object
     */
    public Long ultrasonicRead() {
        try {
            return ultrasonicSensorReader.takeReading();
        } catch (GroveSensorException ex) {
            LOGGER.log(Level.SEVERE, "Robot.ultrasonicRead: Unable to read the UltrasonicSensorReader: GroveSensorException {0}",ex.getMessage());
            return null;
        }            
    }
}
