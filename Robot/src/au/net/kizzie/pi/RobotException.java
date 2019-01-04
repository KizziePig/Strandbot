package au.net.kizzie.pi;

import java.io.Serializable;

/**
 *
 * @author steve
 */
public class RobotException extends Exception implements Serializable {
	private static final long serialVersionUID = -5241554853174409769L;
		
	public RobotException(String message) {
		super(message);
	}
	public RobotException(String message, Exception ex) {
		super(message,ex);
	}
	public RobotException(String message, Throwable th) {
		super(message,th);
	}
}