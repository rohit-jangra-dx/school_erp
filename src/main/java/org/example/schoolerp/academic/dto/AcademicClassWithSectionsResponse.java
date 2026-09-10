package org.example.schoolerp.academic.dto;

import java.util.List;
import java.util.UUID;

public record AcademicClassWithSectionsResponse(
    UUID id, String name, List<ClassSectionSummaryResponse> sections) {}
