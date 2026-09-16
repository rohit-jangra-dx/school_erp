package org.example.schoolerp.academic.exceptions;

public class SectionCapacityExceededException extends RuntimeException {
  public SectionCapacityExceededException(String msg) {
    super(msg);
  }
}
