CREATE EXTENSION IF NOT EXISTS btree_gist;

-- to make sure there are no overlapping years for tenant
ALTER TABLE academic_years
ADD CONSTRAINT ex_academic_year_no_overlap
EXCLUDE USING gist (
    organization_id WITH =,
    daterange(start_date, end_date, '[]') WITH &&
);

-- unique roll_no per section per year (to reuse the roll no starting next month)
ALTER TABLE enrollments
    DROP CONSTRAINT enrollment_class_section_roll_no;

ALTER TABLE enrollments
    ADD CONSTRAINT enrollment_academic_year_class_section_roll_no
        UNIQUE (academic_year_id, class_section_id, roll_no);


CREATE TABLE IF NOT EXISTS attendance (
    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,
    academic_day_id UUID NOT NULL,
    enrollment_id UUID NOT NULL,

    status VARCHAR(60) NOT NULL,
    note VARCHAR(255),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT attendance_academic_day_enrollment
        UNIQUE (academic_day_id, enrollment_id),

    CONSTRAINT fk_attendance_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_attendance_academic_day
        FOREIGN KEY (academic_day_id)
        REFERENCES academic_days(id),

    CONSTRAINT fk_attendance_enrollment
        FOREIGN KEY (enrollment_id)
        REFERENCES enrollments(id)
);
