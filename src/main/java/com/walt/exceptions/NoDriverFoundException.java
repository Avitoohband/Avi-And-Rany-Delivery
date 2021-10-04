package com.walt.exceptions;

public class NoDriverFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "There are no available drivers";

    public NoDriverFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
