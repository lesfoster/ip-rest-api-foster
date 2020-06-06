package com.project.challenge.controller;

import com.project.challenge.model.IpReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

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

    /**
     * CREATE – equivalent to POST, in that a new resources is being created.  Here, a CIDR block will be posted, and
     * the known IP address list should be increased.  This action will change what can be acquired, and listed.
     *
     * @param cidrBlock allocate these values.
     */
    @PostMapping(path="/cidr" + IpAcquisition.CIDR_BLOCK_PATH_NAME, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> createCidrBlock(@PathVariable(CIDR_BLOCK_PATH_VAR) String cidrBlock) {
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        log.debug("Got CIDR block to allocate: {}", cidrBlock);
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
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        log.debug("Got IP address to release: {}", ipAddr);
        // Got some kind of IP address to release.
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
        // Got some kind of IP address requested.
        return responseEntity;
    }

    @GetMapping(path="/ip_states")
    public ResponseEntity<IpReport> listIps() {
        IpReport report = new IpReport();
        report.setCidr("10.0.0.0/24");
        report.setIpStatusList(Collections.emptyList());
        ResponseEntity<IpReport> responseEntity = ResponseEntity.ok(report);

        return responseEntity;
    }
}
