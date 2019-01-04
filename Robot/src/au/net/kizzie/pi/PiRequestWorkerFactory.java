/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.net.kizzie.pi;

import au.net.kizzie.common.WorkerFactory;
import java.net.Socket;

/**
 *  Provides instances of the Worker whenever the server needs one
 * @author steve
 */
public class PiRequestWorkerFactory implements WorkerFactory {
    private Robot robot;
    
    public PiRequestWorkerFactory(Robot robot) {
        this.robot = robot;
    }

    @Override
    public Runnable create(Socket clientSocket) {
            return new PiRequestWorker(clientSocket, robot);
    }
}