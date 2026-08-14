package org.example.schoolerp.organization;

import org.example.schoolerp.core.Base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "organizations")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Organization extends Base{
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationStatus status;

    public Organization(String name) {
        this.name = name;
        this.status = OrganizationStatus.ACTIVE;
    }

    public void activate() {
        this.status = OrganizationStatus.ACTIVE;
    }

    public void suspend() {
        this.status = OrganizationStatus.SUSPENDED;
    }

    public void deactivate() {
        this.status = OrganizationStatus.DEACTIVATED;
    }
}
