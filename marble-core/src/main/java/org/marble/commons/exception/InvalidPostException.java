package org.marble.commons.exception;

public class InvalidPostException extends Exception {
    private static final long serialVersionUID = 420178239099804513L;
    public InvalidPostException() {
        super();
    }
    public InvalidPostException(String message) {
        super(message);
    }
}
