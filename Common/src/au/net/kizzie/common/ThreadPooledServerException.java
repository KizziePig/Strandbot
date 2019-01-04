package au.net.kizzie.common;

import java.io.Serializable;

public class ThreadPooledServerException extends Exception implements Serializable {
    private static final long serialVersionUID = -5241554853174409769L;

    public ThreadPooledServerException(String message) {
            super(message);
    }
    public ThreadPooledServerException(String message, Throwable th) {
            super(message,th);
    }
}
