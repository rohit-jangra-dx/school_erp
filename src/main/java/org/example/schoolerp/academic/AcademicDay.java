package org.example.schoolerp.academic;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;

@Entity
@Table(
    name = "academic_days",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_academic_day_academic_year_date",
          columnNames = {"academic_year_id", "date"})
    })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AcademicDay extends OrganizationOwned {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "academic_year_id", nullable = false)
  private AcademicYear academicYear;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Enumerated(EnumType.STRING)
  @Column(name = "day_type", nullable = false)
  private DayType dayType;

  @Column(name = "note")
  private String note;

  public AcademicDay(AcademicYear academicYear, DayType dayType) {
    this.academicYear = academicYear;
    this.dayType = dayType;
  }

  public void setStatus(DayType dayType) {
    this.dayType = dayType;
  }

  public void setNote(String note) {
    this.note = note;
  }
}
