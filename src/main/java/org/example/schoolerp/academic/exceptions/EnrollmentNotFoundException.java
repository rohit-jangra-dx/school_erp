package org.example.schoolerp.academic.exceptions;

public class EnrollmentNotFoundException extends RuntimeException {
  public EnrollmentNotFoundException(String msg) {
    super(msg);
  }
}
