package au.net.kizzie.pi;

import au.net.kizzie.common.CommonConfig;
import au.net.kizzie.common.ThreadPooledServer;

/**
 * This is the server that actually talks to the Raspberry Pi / Grove Sensors / Stepper Motors / etc on the Robot
 * @author steve
 */
public class PiServer {
    private final PiRequestWorkerFactory piRequestWorkerFactory;
    private final ThreadPooledServer threadPooledServer;
    private final Robot robot;
    
    public PiServer(int port) throws RobotException {
        robot = new Robot();
        piRequestWorkerFactory = new PiRequestWorkerFactory(robot);
        threadPooledServer = new ThreadPooledServer(port, piRequestWorkerFactory);
    }
    
    public void start() {
        threadPooledServer.run();
    }
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Integer port = CommonConfig.getIntProperty(CommonConfig.ROBOT_PORT);
        PiServer piServer = new PiServer(port);
        piServer.start();
    }

}
