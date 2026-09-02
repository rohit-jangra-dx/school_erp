package org.example.schoolerp.student;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;

@Entity
@Table(
    name = "guardians",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_guardian_org_email",
          columnNames = {"organization_id", "email"}),
      @UniqueConstraint(
          name = "uk_guardian_org_phone",
          columnNames = {"organization_id", "phone_no"})
    })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Guardian extends OrganizationOwned {

  @ManyToMany(mappedBy = "guardians")
  private Set<Student> students = new HashSet<>();

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String phoneNo;

  @Column(nullable = false)
  private String relation;

  public Guardian(String fullName, String email, String phoneNo, String relation) {
    this.fullName = fullName;
    this.email = email;
    this.phoneNo = phoneNo;
    this.relation = relation;
  }
}
