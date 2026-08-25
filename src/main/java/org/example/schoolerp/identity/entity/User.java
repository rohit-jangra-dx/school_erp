package org.example.schoolerp.identity.entity;

import java.util.HashSet;
import java.util.Set;

import org.example.schoolerp.core.OrganizationOwned;
import org.example.schoolerp.organization.Organization;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User extends OrganizationOwned{
    
    @Column(nullable = false)
    private String username;

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String username, Organization organization) {
        super(organization);
        this.username = username;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }
}
