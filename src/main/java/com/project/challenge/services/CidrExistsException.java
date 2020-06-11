package com.project.challenge.services;

/**
 * Custom exception to allow precise determination of cause of not being able to set a new CIDR block.
 */
public class CidrExistsException extends Exception {
}
