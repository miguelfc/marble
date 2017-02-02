package org.marble.commons.exception;

public class InvalidStreamingTopicException extends Exception {
    
    private static final long serialVersionUID = 4787325613245024819L;

    public InvalidStreamingTopicException(String message) {
        super(message);
    }

    public InvalidStreamingTopicException(String message, Exception e) {
        super(message, e);
    }

}
