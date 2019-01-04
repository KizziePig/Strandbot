package au.net.kizzie.stepper;
/**
 *
 * @author steve
 */
public interface MovementSteppers {
    /**
     * @param degrees Pass in the number of degrees to turn. Positive => turning right; negative => turning left
     */
    public void turn(double degrees);
    /**
     * @param degrees  Pass in the number of degrees to turn to the right
     */
    //public void turnRight(double degrees);
    /**
     * @param degrees  Pass in the number of degrees to turn to the right
     */
    //public void turnLeft(double degrees);
    
    //public void forward(int steps);
    //public void backward(int steps);
    //public void move(int steps);
    /**
     * Starts moving the requested amount. The move is done in a separate thread so that the caller
     * can keep checking the ultrasonic ranger and can stop the movement if necessary
     * @param distanceInCms 
     */
    public void move(long distanceInCms);
    /**
     * Call this to see if the move is currently underway
     * @return true if the robot is still moving or false if it is stopped
     */
    public boolean isMoving();
    /**
     * Immediately stop the robot
     */
    public void emergencyStop();
    /**
     * @return The distance moved in the last move (in cms)
     */
    public long distanceMoved();
}
