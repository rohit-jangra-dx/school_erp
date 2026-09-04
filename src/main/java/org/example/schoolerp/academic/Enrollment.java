package org.example.schoolerp.academic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;
import org.example.schoolerp.student.Student;

@Entity
@Table(
    name = "enrollments",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "enrollment_academic_year_student",
          columnNames = {"academic_year_id", "student_id"}),
      @UniqueConstraint(
          name = "enrollment_class_section_roll_no",
          columnNames = {"class_section_id", "roll_no"})
    })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Enrollment extends OrganizationOwned {

  // NOTE: keeping it to avoid joins, enrollment by year will be frequent
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "academic_year_id", nullable = false)
  private AcademicYear academicYear;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "class_section_id", nullable = false)
  private ClassSection classSection;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @Column(name = "roll_no", nullable = false)
  private Integer rollNo;

  public Enrollment(
      AcademicYear academicYear, ClassSection classSection, Student student, Integer rollNo) {
    this.academicYear = academicYear;
    this.classSection = classSection;
    this.student = student;
    this.rollNo = rollNo;
  }
}
