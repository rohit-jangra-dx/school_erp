package org.example.schoolerp.academic.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;

/**
 * FIXME: There is a possibility of creating duplicate years. No constraints on the startDate +
 * OrganizationId there to enforce
 */
@Entity
@Table(name = "academic_years")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AcademicYear extends OrganizationOwned {

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  public AcademicYear(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }
}
