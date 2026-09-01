package org.example.schoolerp.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.example.schoolerp.student.dto.CreateStudentRequest;
import org.example.schoolerp.student.service.ImportErrorType;
import org.example.schoolerp.student.service.ImportResult;
import org.example.schoolerp.student.service.StudentCsv;
import org.example.schoolerp.student.service.StudentImportService;
import org.example.schoolerp.student.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class StudentImportServiceTest {

  @Mock private Student student;

  @Mock private StudentService studentService;
  @Mock private StudentRepository studentRepository;

  @InjectMocks private StudentImportService studentImportService;

  @Test
  void importsCorrectlyFormattedFileSuccessfully() throws Exception {
    var file =
        csvFile(
            validRow(
                "John doe", "john@example.com", "2005-08-30", "Male", "Some address", "12", "10"));

    when(studentRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

    ImportResult result = studentImportService.importFromCSV(file);

    assertThat(result.imported()).isEqualTo(1);
    assertThat(result.skipped()).isZero();
    assertThat(result.errors()).isEmpty();

    verify(studentService).create(any(CreateStudentRequest.class));
  }

  @Test
  void importsMultipleValidRowsSuccessfully() throws Exception {
    MockMultipartFile file =
        csvFile(
            validRow(
                "John Doe", "john@example.com", "2005-08-30", "Male", "Address One", "12", "10"),
            validRow(
                "Jane Doe", "jane@example.com", "2006-04-12", "Female", "Address Two", "13", "10"));

    when(studentRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
    when(studentRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());

    ImportResult result = studentImportService.importFromCSV(file);

    assertThat(result.imported()).isEqualTo(2);
    assertThat(result.skipped()).isZero();
    assertThat(result.errors()).isEmpty();

    verify(studentService, times(2)).create(any(CreateStudentRequest.class));
  }

  @Test
  void skipsStudentWhenEmailAlreadyExists() throws Exception {
    MockMultipartFile file =
        csvFile(
            validRow(
                "John Doe", "john@example.com", "2005-08-30", "Male", "Some Address", "12", "10"));
    when(studentRepository.findByEmail("john@example.com")).thenReturn(Optional.of(student));
    ImportResult result = studentImportService.importFromCSV(file);
    assertThat(result.imported()).isZero();
    assertThat(result.skipped()).isEqualTo(1);
    assertThat(result.errors()).hasSize(1);
    assertThat(result.errors().getFirst().type()).isEqualTo(ImportErrorType.DUPLICATE_EMAIL);
    assertThat(result.errors().getFirst().row()).isEqualTo(2);
    verify(studentService, never()).create(any());
  }

  @Test
  void skipsRowWithInvalidColumnCount() throws Exception {
    MockMultipartFile file = csvFile("John Doe,john@example.com,2005-08-30");
    ImportResult result = studentImportService.importFromCSV(file);
    assertThat(result.imported()).isZero();
    assertThat(result.skipped()).isEqualTo(1);
    assertThat(result.errors()).hasSize(1);
    assertThat(result.errors().getFirst().type()).isEqualTo(ImportErrorType.INVALID_COLUMN_COUNT);
    verify(studentService, never()).create(any());
  }

  @Test
  void skipsRowWithInvalidDate() throws Exception {
    MockMultipartFile file =
        csvFile(
            validRow(
                "John Doe", "john@example.com", "not-a-date", "Male", "Some Address", "12", "10"));
    ImportResult result = studentImportService.importFromCSV(file);
    assertThat(result.imported()).isZero();
    assertThat(result.skipped()).isEqualTo(1);
    assertThat(result.errors().getFirst().type()).isEqualTo(ImportErrorType.INVALID_DATE);
    verify(studentService, never()).create(any());
  }

  @Test
  void skipsRowWithInvalidRollNumber() throws Exception {
    MockMultipartFile file =
        csvFile(
            validRow(
                "John Doe",
                "john@example.com",
                "2005-08-30",
                "Male",
                "Some Address",
                "not-a-number",
                "10"));
    ImportResult result = studentImportService.importFromCSV(file);
    assertThat(result.imported()).isZero();
    assertThat(result.skipped()).isEqualTo(1);
    assertThat(result.errors().getFirst().type()).isEqualTo(ImportErrorType.INVALID_NUMBER);
    verify(studentService, never()).create(any());
  }

  @Test
  void skipsRowWithInvalidCurrentClass() throws Exception {
    MockMultipartFile file =
        csvFile(
            validRow(
                "John Doe",
                "john@example.com",
                "2005-08-30",
                "Male",
                "Some Address",
                "12",
                "invalid"));
    ImportResult result = studentImportService.importFromCSV(file);
    assertThat(result.imported()).isZero();
    assertThat(result.skipped()).isEqualTo(1);
    assertThat(result.errors().getFirst().type()).isEqualTo(ImportErrorType.INVALID_NUMBER);
    verify(studentService, never()).create(any());
  }

  @Test
  void ignoresEmptyRows() throws Exception {
    MockMultipartFile file =
        csvFile(
            "",
            " ",
            validRow(
                "John Doe", "john@example.com", "2005-08-30", "Male", "Some Address", "12", "10"));
    when(studentRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
    ImportResult result = studentImportService.importFromCSV(file);
    assertThat(result.imported()).isEqualTo(1);
    assertThat(result.skipped()).isZero();
    assertThat(result.errors()).isEmpty();
  }

  @Test
  void importsValidRowsAndSkipsInvalidRows() throws Exception {
    MockMultipartFile file =
        csvFile(
            validRow(
                "John Doe", "john@example.com", "2005-08-30", "Male", "Some Address", "12", "10"),
            validRow(
                "Bad Date", "bad@example.com", "not-a-date", "Male", "Some Address", "12", "10"),
            validRow(
                "Jane Doe",
                "jane@example.com",
                "2006-04-12",
                "Female",
                "Another Address",
                "13",
                "10"));
    when(studentRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
    when(studentRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
    ImportResult result = studentImportService.importFromCSV(file);
    assertThat(result.imported()).isEqualTo(2);
    assertThat(result.skipped()).isEqualTo(1);
    assertThat(result.errors()).hasSize(1);
    assertThat(result.errors().getFirst().type()).isEqualTo(ImportErrorType.INVALID_DATE);
  }

  @Test
  void throwsExceptionWhenHeadersAreInvalid() {
    MockMultipartFile file = rawCsvFile("name,email,date John Doe,john@example.com,2005-08-30 ");
    assertThatThrownBy(() -> studentImportService.importFromCSV(file))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid CSV headers");
  }

  @Test
  void throwsExceptionWhenFileIsEmpty() {
    MockMultipartFile file = rawCsvFile("");
    assertThatThrownBy(() -> studentImportService.importFromCSV(file))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("CSV file is empty");
  }

  @Test
  void skipsRowWhenStudentCreationFails() throws Exception {
    MockMultipartFile file =
        csvFile(
            validRow(
                "John Doe", "john@example.com", "2005-08-30", "Male", "Some Address", "12", "10"));
    when(studentRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Database failure"))
        .when(studentService)
        .create(any(CreateStudentRequest.class));
    ImportResult result = studentImportService.importFromCSV(file);
    assertThat(result.imported()).isZero();
    assertThat(result.skipped()).isEqualTo(1);
    assertThat(result.errors()).hasSize(1);
    assertThat(result.errors().getFirst().type())
        .isEqualTo(ImportErrorType.STUDENT_CREATION_FAILED);
  }

  @Test
  void createsStudentRequestWithCorrectValues() throws Exception {
    MockMultipartFile file =
        csvFile(
            validRow(
                "John Doe", "john@example.com", "2005-08-30", "Male", "Some Address", "12", "10"));
    when(studentRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
    ImportResult result = studentImportService.importFromCSV(file);
    ArgumentCaptor<CreateStudentRequest> captor =
        ArgumentCaptor.forClass(CreateStudentRequest.class);
    verify(studentService).create(captor.capture());
    CreateStudentRequest request = captor.getValue();
    assertThat(result.imported()).isEqualTo(1);
    assertThat(request.getFullName()).isEqualTo("John Doe");
    assertThat(request.getEmail()).isEqualTo("john@example.com");
    assertThat(request.getDob().toString()).isEqualTo("2005-08-30");
    assertThat(request.getGender()).isEqualTo("Male");
    assertThat(request.getAddress()).isEqualTo("Some Address");
    assertThat(request.getCurrentRollNumber()).isEqualTo(12);
    assertThat(request.getCurrentClass()).isEqualTo(10);
  }

  // Test helpers
  private MockMultipartFile csvFile(String... rows) {

    StringBuilder csv = new StringBuilder(String.join(",", StudentCsv.HEADERS));

    for (String row : rows) {
      csv.append(System.lineSeparator());
      csv.append(row);
    }

    return rawCsvFile(csv.toString());
  }

  private MockMultipartFile rawCsvFile(String content) {

    return new MockMultipartFile(
        "file", "students.csv", "text/csv", content.getBytes(StandardCharsets.UTF_8));
  }

  private String validRow(
      String fullName,
      String email,
      String dob,
      String gender,
      String address,
      String rollNumber,
      String currentClass) {
    return String.join(",", fullName, email, dob, gender, address, rollNumber, currentClass);
  }
}
