package org.example.schoolerp.identity.entity;

import java.util.HashSet;
import java.util.Set;

import org.example.schoolerp.core.Base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Role extends Base{
    
    @Column(nullable = false, updatable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();


    public Role(String name) {
        this.name = name;
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }
}
