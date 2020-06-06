package com.project.challenge.services;

import org.springframework.stereotype.Service;

/**
 * Middle ware for REST interface.  Takes on business logic around IP interactions with persistent store.
 */
@Service
public interface IpStateService {
    void setIpStateAcquired(String ipAddr) throws IpStateServiceException;
    void setIpStateReleased(String ipAddr) throws IpStateServiceException;
}
