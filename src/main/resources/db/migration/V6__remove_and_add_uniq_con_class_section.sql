ALTER TABlE class_sections
    DROP CONSTRAINT class_section_id_teacher;

ALTER TABLE class_sections
    ADD CONSTRAINT class_section_academic_year_teacher
        UNIQUE (academic_year_id, teacher_id);

ALTER TABLE class_sections
    ADD CONSTRAINT class_section_academic_year_class_name
        UNIQUE (academic_year_id, academic_class_id, name);
