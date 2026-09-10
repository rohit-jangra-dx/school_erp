package org.example.schoolerp.fixtures;

import java.time.LocalDate;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.example.schoolerp.academic.repo.AcademicYearRepository;
import org.example.schoolerp.fixtures.TenantFixtures.TenantFixture;
import org.example.schoolerp.organization.Organization;
import org.example.schoolerp.staff.Teacher;
import org.example.schoolerp.staff.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AcademicFixtures {

  @Autowired private AcademicYearRepository academicYearRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private TenantFixtures tenantFixtures;

  public AcademicYear createYear() {
    var year = new AcademicYear(LocalDate.of(2000, 01, 01), LocalDate.of(2001, 12, 31));
    year = academicYearRepository.save(year);

    return year;
  }

  public Teacher createTeacher(Organization org) {
    TenantFixture tenant = tenantFixtures.createUser(org, "test_teacher_", "test_pass");

    var teacher =
        new Teacher(
            tenant.user(),
            "test_teacher_kumar",
            "9900990099",
            "unique@gmail.com",
            LocalDate.of(2000, 01, 01),
            "Male",
            "Test_address");
    teacher = teacherRepository.save(teacher);

    return teacher;
  }
}
