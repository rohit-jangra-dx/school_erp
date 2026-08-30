package org.example.schoolerp.student.service;

public class StudentCsv {
  private StudentCsv() {}

  public static final int EXPECTED_COLUMN_COUNT = 7;

  public static final int FULL_NAME_INDEX = 0;
  public static final int EMAIL_INDEX = 1;
  public static final int DOB_INDEX = 2;
  public static final int GENDER_INDEX = 3;
  public static final int ADDRESS_INDEX = 4;
  public static final int ROLL_NUMBER_INDEX = 5;
  public static final int CURRENT_CLASS_INDEX = 6;

  public static final String FULL_NAME_HEADER = "full_name";
  public static final String EMAIL_HEADER = "email";
  public static final String DOB_HEADER = "dob";
  public static final String GENDER_HEADER = "gender";
  public static final String ADDRESS_HEADER = "address";
  public static final String ROLL_NUMBER_HEADER = "current_roll_number";
  public static final String CURRENT_CLASS_HEADER = "current_class";

  public static final String[] HEADERS = {
    FULL_NAME_HEADER,
    EMAIL_HEADER,
    DOB_HEADER,
    GENDER_HEADER,
    ADDRESS_HEADER,
    ROLL_NUMBER_HEADER,
    CURRENT_CLASS_HEADER
  };
}
