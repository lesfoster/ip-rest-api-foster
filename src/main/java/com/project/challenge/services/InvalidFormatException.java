package com.project.challenge.services;
/**
 * Custom exception to indicate validity assumption has failed.
 */
public class InvalidFormatException extends Exception {
    public InvalidFormatException(String msg, String value) {
        super(String.format(msg, value));
    }
}
