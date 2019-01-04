package au.net.kizzie.test;
import au.net.kizzie.pi.Robot;

/**
 *
 * @author steve
 */
public class TestMoving {

    public static void testTurning() throws Exception {
        System.out.println("Started testing turning");
        Robot robot = new Robot();
        robot.turn(45.0);
        robot.turn(90.0);
        System.out.println("Finished testing turning");
    }    
    
    public static void main(String args[]) throws Exception {
        testTurning();
    }
}
