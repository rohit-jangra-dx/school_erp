package org.example.schoolerp.student.service;

import java.util.List;

public record ImportResult(int imported, int skipped, List<ImportError> errors) {}
