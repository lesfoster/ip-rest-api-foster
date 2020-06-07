package com.project.challenge.controllers;

import com.project.challenge.model.IpReport;
import com.project.challenge.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST ful endpoint controller for IP address management.
 *
 * @author Leslie L. Foster
 */
@RestController
@RequestMapping(path=IpAcquisition.STD_PATH)
public class IpAcquisition {
    private static final String IP_ADDR_PATH_VAR = "ipAddr";
    private static final String IP_ADDR_PATH_NAME = "/{" + IP_ADDR_PATH_VAR + "}";

    private static final String CIDR_BLOCK_PATH_VAR = "cidr";
    private static final String CIDR_BLOCK_PATH_NAME = "/{" + CIDR_BLOCK_PATH_VAR + "}";

    public static final String STD_PATH = "ip";

    private Logger log = LogManager.getLogger(IpAcquisition.class);

    private IpStateService ipStateService;
    private CidrStateService cidrStateService;

    /**
     * Gets constructed with a full-prepared state service to use.
     *
     * @param ipStateService used for storing the state back/reading states of IPs and CIDR blocks.
     */
    @Autowired
    public IpAcquisition(IpStateService ipStateService, CidrStateService cidrStateService) {
        this.ipStateService = ipStateService;
        this.cidrStateService = cidrStateService;
    }

    /**
     * CREATE – equivalent to POST, in that a new resources is being created.  Here, a CIDR block will be posted, and
     * the known IP address list should be increased.  This action will change what can be acquired, and listed.
     *
     * @param ipAddr starting position for CIDR block.
     * @param cidrBlockMaskSize allocate these values (after the slash).
     */
    @PostMapping(path="/cidr" + IpAcquisition.IP_ADDR_PATH_NAME + IpAcquisition.CIDR_BLOCK_PATH_NAME)
    public ResponseEntity<Void> createCidrBlock(
            @PathVariable(IP_ADDR_PATH_VAR)String ipAddr,
            @PathVariable(CIDR_BLOCK_PATH_VAR) String cidrBlockMaskSize) {

        String cidrBlockStr = String.format("%s/%s", ipAddr, cidrBlockMaskSize);
        log.debug("Got CIDR block to allocate: {}", cidrBlockStr);
        ResponseEntity<Void> responseEntity;
        try {
            cidrStateService.setCidrBlock(cidrBlockStr);
            responseEntity = ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (CidrExistsException cee) {
            responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (InvalidFormatException ife) {
            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return responseEntity;
    }

    /**
     * RELEASE – equivalent to PUT, in that a resource is to be changed.  Not changing entire set—only a single IP
     * address.  Should not return any value except success.
     *
     * @param ipAddr attempt to give up use of this IP address and return it to the pool.
     * @return outcome of the attempt.
     */
    @PutMapping(path="/freed" + IpAcquisition.IP_ADDR_PATH_NAME)
    public ResponseEntity<Void> releaseIp(@PathVariable(IP_ADDR_PATH_VAR) String ipAddr) {
        ResponseEntity<Void> responseEntity;
        log.debug("Got IP address to release: {}", ipAddr);
        try {
            ipStateService.setIpStateReleased(ipAddr);
            responseEntity = ResponseEntity.ok().build();
        } catch (IpStateServiceException ipse) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return responseEntity;
    }

    /**
     * ACQUIRE – equivalent to PUT, in that a resource is being changed.  However, no caller should ever expect to push
     * the entire state of the content.  Only one such IP address.  Acquire will transform the backend state
     * (persistent state) by marking one IP address as “acquired”.  This should return a value (the IP address).
     *
     * @param ipAddr attempt to obtain this IP address for use of caller.
     * @return outcome of the attempt.
     */
    @PutMapping(path="/acquired" + IpAcquisition.IP_ADDR_PATH_NAME)
    public ResponseEntity<Void> acquireIp(@PathVariable(IP_ADDR_PATH_VAR) String ipAddr) {
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        log.debug("Got IP address request: {}", ipAddr);
        try {
            ipStateService.setIpStateAcquired(ipAddr);
            responseEntity = ResponseEntity.ok().build();
        } catch (IpStateServiceException ipse) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return responseEntity;
    }

    /**
     * LIST - Return states of all the IP addresses managed herein.
     *
     * @return IP Report telling if each and every IP address has been acquired.
     */
    @GetMapping(path="/ip_states")
    public ResponseEntity<IpReport> listIps() {
        log.debug("Report request.");
        ResponseEntity<IpReport> responseEntity;
        try {
            IpReport report = ipStateService.getIpReport();
            responseEntity =  ResponseEntity.ok(report);
        } catch (IpStateServiceException ipse) {
            responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return responseEntity;
    }
}
