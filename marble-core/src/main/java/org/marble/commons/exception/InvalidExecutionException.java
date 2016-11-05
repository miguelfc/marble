package org.marble.commons.exception;

public class InvalidExecutionException extends Exception {
    public InvalidExecutionException(String message) {
        super(message);
    }
    public InvalidExecutionException(String message, Exception e) {
        super(message, e);
    }

    private static final long serialVersionUID = 4509093896055366676L;
}
