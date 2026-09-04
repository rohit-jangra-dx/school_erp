package org.example.schoolerp.academic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;

@Entity
@Table(name = "academic_classes")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AcademicClass extends OrganizationOwned {

  @Column(name = "name", nullable = false, updatable = false)
  private String name;

  public AcademicClass(String name) {
    this.name = name;
  }
}
