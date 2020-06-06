package com.project.challenge.services;

/**
 * Custom exception wrapper to encapsulate all possible exceptions during IP State Service interactions.
 */
public class IpStateServiceException extends Exception {
    public IpStateServiceException(Exception wrapped) {
        super(wrapped);
    }
}
