package org.example.schoolerp.academic;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.schoolerp.core.OrganizationOwned;

@Entity
@Table(name = "academic_years")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AcademicYear extends OrganizationOwned {

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;
}
