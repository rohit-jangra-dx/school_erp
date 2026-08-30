package org.example.schoolerp.student.service;

public record ImportError(int row, ImportErrorType type, String field, String message) {}
