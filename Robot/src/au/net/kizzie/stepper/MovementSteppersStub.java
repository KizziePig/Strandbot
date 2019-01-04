package au.net.kizzie.stepper;
/**
 *
 * @author steve
 */
public class MovementSteppersStub implements MovementSteppers {
    protected MovementSteppersStub() {
        
    }
    @Override
    public void turn(double degrees) {
        
    }
    @Override
    public void move(long distanceInCms) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMoving() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void emergencyStop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long distanceMoved() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
