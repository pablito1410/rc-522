package com.pablo.rc522.exception;

public class CommunicationException extends RuntimeException {

    public CommunicationException() {
    }

    public CommunicationException(final String message) {
        super(message);
    }
}
