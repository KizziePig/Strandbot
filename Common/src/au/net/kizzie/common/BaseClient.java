package au.net.kizzie.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Call this to attach to the Pi (or simulator) and carry out the request
 * @author steve
 */
public class BaseClient {
    private static final Logger LOGGER = Logger.getLogger(BaseClient.class.getName());
    /**
     * This internal function is called by the public functions above
     * @param host
     * @param port
     * @param request
     * @return 
     * @throws au.net.kizzie.common.BaseClientException 
     */
    protected static String call(String host, int port, String request) throws BaseClientException {
        Socket requestSocket = null;
        PrintStream out = null;
        BufferedReader in = null;
        try{
            requestSocket = new Socket(host, port);
            out = new PrintStream(requestSocket.getOutputStream());
            out.flush();
            out.println(request);
            out.flush();
            in = new BufferedReader(new  InputStreamReader(requestSocket.getInputStream()));
            String response = (String) in.readLine();
            return response;
        } catch(UnknownHostException ex) {
            LOGGER.log(Level.SEVERE,"BaseClient.call: UnknownHostException caught: {0} - ignoring request",ex.getMessage());
            throw new BaseClientException("BaseClient.call: UnknownHostException caught: "+ex.getMessage(),ex);
        } catch(IOException ex) {
            LOGGER.log(Level.SEVERE,"BaseClient.call: IOException caught: {0}",ex.getMessage());
            throw new BaseClientException("BaseClient.call: IOException caught: "+ex.getMessage(),ex);
        } finally {
            try {
                if (in != null) 
                    in.close();
            }
            catch(IOException ex) {
            }
            try {
                if (out != null) 
                    out.close();
            }
            catch(Exception ex) {
            }
            try {
                if (requestSocket != null)
                    requestSocket.close();
            }
            catch(IOException ex) {
                LOGGER.log(Level.SEVERE,"BaseClient.call: IOException caught: {0} - ignoring",ex.getMessage());
                throw new BaseClientException("BaseClient.call: IOException caught: "+ex.getMessage(),ex);
            }
        }
    }
 
}
