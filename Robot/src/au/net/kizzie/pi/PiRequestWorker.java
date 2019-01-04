package au.net.kizzie.pi;
import au.net.kizzie.common.CommonConfig;
import au.net.kizzie.common.StringUtil;
import au.net.kizzie.sensors.UltrasonicSensorReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An instance of this worker thread is instantiated to handle each incoming socket connection from a client.
 * This worker establishes input / output streams with the client, then reads a request, processes it, and responds to it, before closing the streams.
 */
public class PiRequestWorker implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(PiRequestWorker.class.getName());
    protected Socket clientSocket;
    protected Robot robot;
    protected UltrasonicSensorReader ultrasonicSensorReader;
    
    public PiRequestWorker(Socket clientSocket, Robot robot) {
        this.clientSocket = clientSocket;
        this.robot = robot;
    }

    @Override
    public void run() {
        LOGGER.log(Level.INFO,"PiRequestWorker.run: Started");
    	PrintWriter out = null;
    	BufferedReader in = null;
    	try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            String response;
            while ((inputLine = in.readLine()) != null) {
                LOGGER.log(Level.INFO,"PiRequestWorker.run: line read {0}",inputLine);
                PiRequest request = isValidRequest(inputLine);
                if (request == null) {
                    LOGGER.log(Level.WARNING,"PiRequestWorker.run: Given request {0} is invalid - responding with InvalidRequest",inputLine);
                    out.println(CommonConfig.getProperty(CommonConfig.ROBOT_RESPONSE_INVALID_REQUEST)+" "+inputLine);
                } else {
                    LOGGER.log(Level.WARNING,"PiRequestWorker.run: Given request {0} is valid ",inputLine);
                    processRequest(request, inputLine, out);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,"PiRequestWorker.run: IOException caught: {0} - responding with failure",ex.getMessage());
            if (out != null)
                out.println(CommonConfig.getProperty(CommonConfig.ROBOT_RESPONSE_FAILURE_RESPONSE)+" "+ex.getMessage());
     	} finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
            }
            try {
                if (in != null)
                    in.close();
            } catch (IOException ex) {
            }
    	}
        LOGGER.log(Level.INFO,"PiRequestWorker.run: Finished");
    }
    
    private void processRequest(PiRequest request, String inputLine, PrintWriter out) {
        switch (request.getPiRequestType()) {
        case ultrasonicRead:  // ultrasonicRead has no parameters
            Long distance = robot.ultrasonicRead();
            String distanceString = distance == null? CommonConfig.getProperty(CommonConfig.ROBOT_RESPONSE_NO_OBJECT) : String.valueOf(distance);
            String response = distanceString+" ."; // NOTE: Response must be followed by a space and then a full stop!!!
            LOGGER.log(Level.INFO, "PiRequestWorker.run: Responding to ultrasonic read request {0} with {1}",new Object[] {request.getPiRequestType(),response});
            out.println(response);
            break;
        default: 
            LOGGER.log(Level.INFO,"PiRequestWorker.run: Invalid request passed in: requestType is '{0}' - responding with invalid request",request.getPiRequestType());
            out.println(CommonConfig.getProperty(CommonConfig.ROBOT_RESPONSE_INVALID_REQUEST)+" "+inputLine);
            break;
        }
    }
    
    /**
     * Returns the valid PiRequest object or null if it is invalid
     * @param input The request input line as a string
     * @return The valid interpreted request or null if the given request input line is invalid
     */
    public PiRequest isValidRequest(String input) {
        if (StringUtil.isBlankString(input) 
                || input.length() < 4
                || input.charAt(0) != 34 
                || input.charAt(input.length()-1) != 46 
                || input.charAt(input.length()-2) != 32 
                || input.charAt(input.length()-3) != 34) {  
            LOGGER.log(Level.WARNING,"PiRequestWorker.isRequestValid: Request is not valid because it must not be blank and must start with a double quote and end with a space followed by a double quote followed by a full stop but I was given {0} - returning null",input);
            return null;
        }
        String requestString = input.substring(1,input.length()-3);
    	if (requestString.startsWith(CommonConfig.getProperty(CommonConfig.ROBOT_REQUEST_ULTRASONIC_READ))) {
            PiRequest request = new PiRequest(PiRequestType.ultrasonicRead, requestString);
            return request;
    	}
        return null;
    }
}
