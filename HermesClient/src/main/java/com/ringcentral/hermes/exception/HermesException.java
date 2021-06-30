package com.ringcentral.hermes.exception;

public class HermesException extends RuntimeException {

    public HermesException(String msg) {
        super(msg);
    }

    public HermesException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
