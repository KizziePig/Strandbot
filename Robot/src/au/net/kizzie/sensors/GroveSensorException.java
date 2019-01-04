package au.net.kizzie.sensors;

import java.io.Serializable;

/**
 *
 * @author steve
 */
public class GroveSensorException extends Exception implements Serializable {
    private static final long serialVersionUID = -5241554853174409789L;
    public GroveSensorException(String msg) {
        super(msg);
    }
    public GroveSensorException(String msg, Throwable th) {
        super(msg,th);
    }
}
