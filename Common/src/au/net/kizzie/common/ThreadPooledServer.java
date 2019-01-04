package au.net.kizzie.common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadPooledServer implements Runnable{
    protected int          serverPort;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);
    protected WorkerFactory workerFactory;
    
    /**
     * To use this class:
     * @param port Pass in the port the server should listen on
     * @param workerFactory An implementation of the WorkerFactory interface in a class to provide instances of the worker thread which is executed whenever a client request comes in.
     */
    public ThreadPooledServer(int port, WorkerFactory workerFactory) {
        this.serverPort = port;
        this.workerFactory = workerFactory;
    }

    @Override
    public void run() {
        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }
        
        try {
            openServerSocket();

            while(! isStopped()){
                Socket clientSocket = null;
                try {
                    clientSocket = this.serverSocket.accept();
                } catch (IOException ex) {
                    if(isStopped()) {
                        System.err.println("ThreadPooledServer.run: Server Stopped - exiting") ;
                        return;
                    }
                    throw new ThreadPooledServerException("ThreadPooledServer.run: Error accepting client connection", ex);
                }
                this.threadPool.execute(workerFactory.create(clientSocket));
            }
        } catch (ThreadPooledServerException ex) {
            System.err.println("ThreadPooledServer.run: LocationException caught: "+ex.getMessage()+" - exiting server");
        }
        this.threadPool.shutdown();
        System.err.println("ThreadPooledServer.run: Server exiting") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() throws ThreadPooledServerException {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException ex) {
            throw new ThreadPooledServerException("ThreadPooledServer.openServerSocket: Error closing server", ex);
        }
    }

    private void openServerSocket() throws ThreadPooledServerException {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException ex) {
            throw new ThreadPooledServerException("ThreadPooledServer.openServerSocket: Cannot open port "+serverPort, ex);
        }
    }
}
