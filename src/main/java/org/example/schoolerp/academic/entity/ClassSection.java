package org.example.schoolerp.academic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;
import org.example.schoolerp.staff.Teacher;

/**
 * NOTE: This is somethign to remind myself as i can't remember right now, there is somethign wrong
 * or missing in the constraints. found one -> there is no uniquness constraint on name + class_id
 * as section names should be unique per class
 */
@Entity
@Table(
    name = "class_sections",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "class_section_id_teacher",
          columnNames = {"id", "teacher_id"})
    })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ClassSection extends OrganizationOwned {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "academic_class_id", nullable = false)
  private AcademicClass academicClass;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "academic_year_id", nullable = false)
  private AcademicYear academicYear;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer room;

  @Column(nullable = false)
  private Integer capacity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "teacher_id", nullable = false)
  private Teacher teacher;

  public ClassSection(
      AcademicClass academicClass,
      AcademicYear academicYear,
      Teacher teacher,
      String name,
      Integer room,
      Integer capacity) {
    this.academicClass = academicClass;
    this.academicYear = academicYear;
    this.teacher = teacher;
    this.name = name;
    this.room = room;
    this.capacity = capacity;
  }
}
