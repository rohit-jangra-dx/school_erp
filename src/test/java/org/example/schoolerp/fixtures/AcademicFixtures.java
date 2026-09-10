package org.example.schoolerp.fixtures;

import java.util.UUID;
import net.datafaker.Faker;
import org.example.schoolerp.academic.dto.CreateAcademicClassRequest;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
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
  private final Faker faker = new Faker();

  @Autowired private AcademicYearRepository academicYearRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private TenantFixtures tenantFixtures;

  public AcademicYear createYear() {
    var start = faker.timeAndDate().birthday();
    var end = start.plusYears(1).minusDays(-1);
    var year = new AcademicYear(start, end);

    year = academicYearRepository.save(year);

    return year;
  }

  public Teacher createTeacher(Organization org) {
    TenantFixture tenant = tenantFixtures.createUser(org, "test_teacher_", "test_pass");

    var teacher =
        new Teacher(
            tenant.user(),
            faker.name().fullName(),
            faker.phoneNumber().cellPhone(),
            faker.internet().emailAddress(),
            faker.timeAndDate().birthday(),
            faker.options().option("Male", "Female"),
            faker.address().fullAddress());
    teacher = teacherRepository.save(teacher);

    return teacher;
  }

  public CreateAcademicClassRequest createAcademicClassRequest() {
    var className = String.valueOf(faker.number().numberBetween(1, 10));
    return new CreateAcademicClassRequest(className);
  }

  public CreateClassSectionRequest createClassSectionRequest(
      UUID academicYearId, UUID teacherId, Integer nameCounter) {
    String sectionName = String.valueOf((char) ('A' + nameCounter));

    return new CreateClassSectionRequest(
        academicYearId,
        teacherId,
        sectionName,
        faker.number().numberBetween(1, 20),
        faker.number().numberBetween(10, 50));
  }
}
