package org.example.schoolerp.student;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;
import org.example.schoolerp.identity.entity.User;

@Entity
@Table(name = "students")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Student extends OrganizationOwned {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private User user;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "dob", nullable = false)
  private LocalDate dob;

  private String gender;

  private String address;

  private Integer currentRollNumber;

  @ManyToMany
  @JoinTable(
      name = "student_guardians",
      joinColumns = @JoinColumn(name = "student_id"),
      inverseJoinColumns = @JoinColumn(name = "guardian_id"))
  private Set<Guardian> guardians = new HashSet<>();

  /**
   * FIXME: current i am just passing fixed static values, but class can be associated with the
   * class Entity
   */
  private Integer currentClass;

  public Student(
      User user,
      String fullName,
      String email,
      LocalDate dob,
      String gender,
      String address,
      Integer currentRollNumber,
      Integer currentClass) {
    this.user = user;
    this.fullName = fullName;
    this.email = email;
    this.dob = dob;
    this.gender = gender;
    this.address = address;
    this.currentRollNumber = currentRollNumber;
    this.currentClass = currentClass;
  }

  public void addGuardian(Guardian guardian) {
    guardians.add(guardian);
    guardian.getStudents().add(this);
  }

  public void removeGuardian(Guardian guardian) {
    guardians.remove(guardian);
    guardian.getStudents().remove(this);
  }
}
