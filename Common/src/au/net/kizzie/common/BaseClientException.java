package au.net.kizzie.common;

import java.io.Serializable;

/**
 *
 * @author steve
 */
public class BaseClientException extends Exception implements Serializable {
	private static final long serialVersionUID = -5241554853174409769L;
		
	public BaseClientException(String message) {
		super(message);
	}
	public BaseClientException(String message, Exception ex) {
		super(message,ex);
	}
	public BaseClientException(String message, Throwable th) {
		super(message,th);
	}
}