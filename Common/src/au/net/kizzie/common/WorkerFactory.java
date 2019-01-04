package au.net.kizzie.common;

import java.net.Socket;

/**
 * Implement this to create instances of the worker process to run in the thread pooled server
 */
public interface WorkerFactory {
	public Runnable create(Socket clientSocket);
}
