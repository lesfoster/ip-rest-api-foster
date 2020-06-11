package com.project.challenge.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * CIDR definition.  Tells the current CIDR block in use for this whole schema.
 */
@Entity
public class CidrDef {
    @Id
    private String cidr;

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }
}
