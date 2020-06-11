package com.project.challenge.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Database-holder class for very simple bit-wise store of acquired/free indicators for IP addresses.
 */
@Entity
public class CidrBitBlock {
    @Id
    private
    Integer id;

    private String encodedBits;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEncodedBits() {
        return encodedBits;
    }

    public void setEncodedBits(String encodedBits) {
        this.encodedBits = encodedBits;
    }
}
