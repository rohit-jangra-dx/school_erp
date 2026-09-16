package org.example.schoolerp.core;

public class InvalidQueryParameterException extends RuntimeException {
  public InvalidQueryParameterException(String msg) {
    super(msg);
  }
}
