package org.example.schoolerp.identity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.Base;

@Entity
@Table(name = "permissions")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Permission extends Base {

  @Column(nullable = false, updatable = false, unique = true)
  private String name;

  public Permission(String name) {
    this.name = name;
  }
}
