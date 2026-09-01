package org.example.schoolerp.student;

import com.opencsv.exceptions.CsvValidationException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.schoolerp.student.dto.CreateStudentRequest;
import org.example.schoolerp.student.dto.CreateStudentResponse;
import org.example.schoolerp.student.service.ImportResult;
import org.example.schoolerp.student.service.StudentImportService;
import org.example.schoolerp.student.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

  private final StudentService studentService;
  private final StudentImportService studentImportService;

  @PostMapping("")
  public ResponseEntity<CreateStudentResponse> createStudent(
      @Valid @RequestBody CreateStudentRequest studentRequest) {

    var res = studentService.create(studentRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(res);
  }

  @GetMapping("/import/template")
  public void getMethodName(HttpServletResponse response) throws IOException {

    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition", "attachment; filename=\"students-template.csv\"");

    studentImportService.createTemplate(response.getWriter());
  }

  @PostMapping("/import")
  public ResponseEntity<ImportResult> createStudents(@RequestParam("file") MultipartFile file)
      throws IOException, CsvValidationException {
    if (file.isEmpty()) {
      throw new IllegalArgumentException(" Uploaded file is empty");
    }

    return ResponseEntity.ok(studentImportService.importFromCSV(file));
  }
}
