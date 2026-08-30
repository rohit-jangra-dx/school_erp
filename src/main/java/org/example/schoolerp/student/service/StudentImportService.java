package org.example.schoolerp.student.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schoolerp.student.StudentRepository;
import org.example.schoolerp.student.dto.CreateStudentRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentImportService {
  private final StudentRepository studentRepository;
  private final StudentService studentService;

  public void createTemplate(Writer writer) throws IOException {
    writer.write(String.join(",", StudentCsv.HEADERS));
    writer.write(System.lineSeparator());
    writer.flush();
  }

  public ImportResult importFromCSV(MultipartFile file) throws IOException, CsvValidationException {
    List<ImportError> errors = new ArrayList<>();
    int imported = 0;
    int skipped = 0;

    try (CSVReader reader =
        new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

      String[] headers = reader.readNext();

      validateHeaders(headers);

      String[] columns;
      int rowNum = 1;

      while ((columns = reader.readNext()) != null) {
        rowNum++;

        if (isEmpty(columns)) {
          continue;
        }

        if (columns.length != StudentCsv.EXPECTED_COLUMN_COUNT) {
          errors.add(
              new ImportError(
                  rowNum,
                  ImportErrorType.INVALID_COLUMN_COUNT,
                  null,
                  "Expected "
                      + StudentCsv.EXPECTED_COLUMN_COUNT
                      + " columns but found "
                      + columns.length));

          skipped++;
          continue;
        }

        try {
          var studentRequest = createStudentRequest(columns);

          String email = columns[StudentCsv.EMAIL_INDEX].trim();
          if (studentRepository.findByEmail(email).isPresent()) {
            errors.add(
                new ImportError(
                    rowNum,
                    ImportErrorType.DUPLICATE_EMAIL,
                    StudentCsv.EMAIL_HEADER,
                    "A student with this email already exists"));

            skipped++;
            continue;
          }

          studentService.create(studentRequest);

        } catch (NumberFormatException e) {
          errors.add(
              new ImportError(
                  rowNum,
                  ImportErrorType.INVALID_NUMBER,
                  null,
                  "One or more numeric fields contain an invalid value"));
          skipped++;

        } catch (DateTimeParseException e) {
          errors.add(
              new ImportError(
                  rowNum,
                  ImportErrorType.INVALID_DATE,
                  StudentCsv.DOB_HEADER,
                  "Date must be in yyyy-MM-dd format"));
          skipped++;

        } catch (Exception e) {
          log.error("Failed to import student at row {}", rowNum, e);

          errors.add(
              new ImportError(
                  rowNum,
                  ImportErrorType.STUDENT_CREATION_FAILED,
                  null,
                  "Failed to create student"));

          skipped++;
        }
      }
    }

    log.info("CSV import complete: {} imported, {} skipped", imported, skipped);
    return new ImportResult(imported, skipped, errors);
  }

  private void validateHeaders(String[] headers) {
    if (headers == null) {
      throw new IllegalArgumentException("CSV file is empty");
    }

    @SuppressWarnings("null")
    String[] normalizedHeaders = Arrays.stream(headers).map(String::trim).toArray(String[]::new);

    if (!Arrays.equals(normalizedHeaders, StudentCsv.HEADERS)) {

      throw new IllegalArgumentException("Invalid CSV headers");
    }
  }

  private CreateStudentRequest createStudentRequest(String[] columns) {
    var request = new CreateStudentRequest();

    request.setFullName(columns[StudentCsv.FULL_NAME_INDEX].trim());
    request.setEmail(columns[StudentCsv.EMAIL_INDEX].trim());
    request.setDob(LocalDate.parse(columns[StudentCsv.DOB_INDEX].trim()));
    request.setGender(columns[StudentCsv.GENDER_INDEX].trim());
    request.setAddress(columns[StudentCsv.ADDRESS_INDEX].trim());
    request.setCurrenRollNumber(Integer.parseInt(columns[StudentCsv.ROLL_NUMBER_INDEX].trim()));
    request.setCurrentClass(Integer.parseInt(columns[StudentCsv.CURRENT_CLASS_INDEX].trim()));

    return request;
  }

  private boolean isEmpty(String[] columns) {
    return Arrays.stream(columns).allMatch(value -> value == null || value.isBlank());
  }
}
