package org.marble.commons.exception;

public class InvalidUserException extends RuntimeException {

	private static final long serialVersionUID = -3558091111038053189L;

	public InvalidUserException(String s) {
        super(s);
    }
}
