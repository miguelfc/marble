package org.marble.processor.stanford.exception;

public class InvalidMessageException extends Exception {
    private static final long serialVersionUID = 420178239099804513L;
    public InvalidMessageException() {
        super();
    }
    public InvalidMessageException(String message) {
        super(message);
    }
}
