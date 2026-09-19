package org.example.schoolerp.fixtures;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import net.datafaker.Faker;
import org.example.schoolerp.academic.dto.CreateAcademicCalendarRequest;
import org.example.schoolerp.academic.dto.CreateAcademicClassRequest;
import org.example.schoolerp.academic.dto.CreateClassSectionRequest;
import org.example.schoolerp.academic.dto.CreateEnrollmentRequest;
import org.example.schoolerp.academic.dto.UpdateAcademicClassRequest;
import org.example.schoolerp.academic.dto.UpdateAcademicDayRequest;
import org.example.schoolerp.academic.dto.UpdateAcademicDaybyDateRequest;
import org.example.schoolerp.academic.dto.UpdateClassSectionRequest;
import org.example.schoolerp.academic.dto.UpdateEnrollmentRequest;
import org.example.schoolerp.academic.entity.AcademicClass;
import org.example.schoolerp.academic.entity.AcademicYear;
import org.example.schoolerp.academic.entity.ClassSection;
import org.example.schoolerp.academic.entity.DayType;
import org.example.schoolerp.academic.repo.AcademicClassRepository;
import org.example.schoolerp.academic.repo.AcademicYearRepository;
import org.example.schoolerp.academic.repo.ClassSectionRepository;
import org.example.schoolerp.fixtures.TenantFixtures.TenantFixture;
import org.example.schoolerp.organization.Organization;
import org.example.schoolerp.staff.Teacher;
import org.example.schoolerp.staff.TeacherRepository;
import org.example.schoolerp.student.Student;
import org.example.schoolerp.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AcademicFixtures {
  private final Faker faker = new Faker();

  @Autowired private AcademicYearRepository academicYearRepository;
  @Autowired private TeacherRepository teacherRepository;
  @Autowired private AcademicClassRepository academicClassRepository;
  @Autowired private ClassSectionRepository classSectionRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private TenantFixtures tenantFixtures;

  public AcademicYear createAcademicYear() {
    var start = faker.timeAndDate().birthday();
    var end = start.plusYears(1).minusDays(1);
    var year = new AcademicYear(start, end);

    year = academicYearRepository.save(year);

    return year;
  }

  public AcademicYear createAcademicYear(LocalDate startDate) {
    var end = startDate.plusYears(1).minusDays(1);
    var year = new AcademicYear(startDate, end);

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

  public Student createStudent(Organization org) {
    var userFixture =
        tenantFixtures.createUser(
            org, faker.credentials().username(), faker.credentials().password());

    var student =
        new Student(
            userFixture.user(),
            faker.name().fullName(),
            faker.internet().emailAddress(),
            faker.timeAndDate().birthday(),
            faker.options().option("Male", "Female"),
            faker.address().fullAddress(),
            faker.number().numberBetween(1, 30),
            faker.number().numberBetween(1, 10));
    studentRepository.save(student);
    return student;
  }

  public AcademicClass createAcademicClass() {
    var cls = new AcademicClass(String.valueOf(faker.number().numberBetween(1, 10)));
    academicClassRepository.save(cls);
    return cls;
  }

  public ClassSection createClassSection(
      AcademicClass academicClass, AcademicYear year, Teacher teacher, int nameCounter) {

    String sectionName = String.valueOf((char) ('A') + nameCounter);
    var section =
        new ClassSection(
            academicClass,
            year,
            teacher,
            sectionName,
            faker.number().numberBetween(1, 10),
            faker.number().numberBetween(30, 50));
    classSectionRepository.save(section);
    return section;
  }

  //   requests

  public CreateAcademicCalendarRequest createAcademicCalendarRequest() {
    var startDate = faker.timeAndDate().birthday();
    var endDate = startDate.plusYears(1).minusDays(1);

    return new CreateAcademicCalendarRequest(startDate, endDate);
  }

  /**
   * use it for mass continuous years creation
   *
   * @param yearOffset
   * @return CreateAcademicCalendarRequest
   */
  public CreateAcademicCalendarRequest createAcademicCalendarRequest(int yearOffset) {
    LocalDate startDate = LocalDate.of(2020 + yearOffset, 4, 1);
    LocalDate endDate = startDate.plusYears(1).minusDays(1);

    return new CreateAcademicCalendarRequest(startDate, endDate);
  }

  public UpdateAcademicDayRequest updateAcademicDayRequest() {

    return new UpdateAcademicDayRequest(
        faker
            .options()
            .option(
                DayType.HOLIDAY, DayType.HALF_DAY, DayType.EVENT, DayType.WEEKEND, DayType.WORKING),
        "");
  }

  public UpdateAcademicDaybyDateRequest updateAcademicDaybyDateRequest(
      LocalDate startDate, LocalDate endDate) {
    // random but still it will belong to the calendar
    LocalDate randomDate =
        startDate.plusDays(
            faker.random().nextLong(ChronoUnit.DAYS.between(startDate, endDate) + 1));

    return new UpdateAcademicDaybyDateRequest(
        randomDate,
        faker
            .options()
            .option(
                DayType.HOLIDAY, DayType.HALF_DAY, DayType.EVENT, DayType.WEEKEND, DayType.WORKING),
        "");
  }

  public UpdateAcademicClassRequest updateAcademicClassRequest() {
    return new UpdateAcademicClassRequest(String.valueOf(faker.number().numberBetween(1, 10)));
  }

  public UpdateClassSectionRequest updateClassSectionRequest(int nameCounter) {
    String sectionName = String.valueOf((char) ('A') + nameCounter);
    return new UpdateClassSectionRequest(
        sectionName,
        faker.number().numberBetween(1, 10),
        faker.number().numberBetween(30, 50),
        null);
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

  public CreateEnrollmentRequest createEnrollmentRequest(
      UUID yearId, UUID sectionId, UUID studentId, int rollNoIndex) {
    return new CreateEnrollmentRequest(sectionId, studentId, rollNoIndex + 1);
  }

  public UpdateEnrollmentRequest updateEnrollmentRequest(UUID sectionId) {
    return new UpdateEnrollmentRequest(sectionId);
  }
}
