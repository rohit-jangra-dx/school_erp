package org.example.schoolerp.core;


import org.example.schoolerp.organization.Organization;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class OrganizationOwned extends Base{

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    
}
