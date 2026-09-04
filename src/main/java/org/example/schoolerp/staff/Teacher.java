package org.example.schoolerp.staff;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;
import org.example.schoolerp.identity.entity.User;

@Entity
@Table(
    name = "teachers",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_teacher_org_phone",
          columnNames = {"organization_id", "phone_no"}),
      @UniqueConstraint(
          name = "uk_teacher_org_email",
          columnNames = {"organization_id", "phone_no"})
    })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Teacher extends OrganizationOwned {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "phone_no", nullable = false)
  private String phoneNo;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private LocalDate dob;

  private String gender;

  private String address;

  public Teacher(
      User user,
      String fullName,
      String phoneNo,
      String email,
      LocalDate dob,
      String gender,
      String address) {
    this.user = user;
    this.fullName = fullName;
    this.phoneNo = phoneNo;
    this.email = email;
    this.dob = dob;
    this.gender = gender;
    this.address = address;
  }
}
