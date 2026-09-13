package org.example.schoolerp.academic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;

@Entity
@Table(
    name = "attendance",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "attendance_academic_day_enrollment",
          columnNames = {"academic_day_id", "enrollment_id"})
    })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Attendance extends OrganizationOwned {

  @ManyToOne(optional = false)
  @JoinColumn(name = "academic_day_id", nullable = false, updatable = false)
  private AcademicDay academicDay;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "enrollment_id", nullable = false, updatable = false)
  private Enrollment enrollment;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AttendanceStatus status;

  @Column(nullable = true)
  private String note;
}
