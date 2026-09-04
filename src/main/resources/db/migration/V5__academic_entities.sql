CREATE TABLE IF NOT EXISTS teachers (
    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,

    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_no VARCHAR(50) NOT NULL,
    dob DATE NOT NULL,
    gender VARCHAR(255),
    address VARCHAR(255),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_teachers_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_teachers_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT uk_teacher_org_phone
        UNIQUE (organization_id, phone_no),

    CONSTRAINT uk_teacher_org_email
        UNIQUE (organization_id, email)
);

CREATE TABLE IF NOT EXISTS academic_years (
    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_academic_years_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
);


CREATE TABLE IF NOT EXISTS academic_days (
    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,
    academic_year_id UUID NOT NULL,

    date DATE NOT NULL,
    day_type VARCHAR(60) NOT NULL,
    note VARCHAR(255),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_academic_days_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_academic_days_academic_year
        FOREIGN KEY (academic_year_id)
        REFERENCES academic_years(id),

    CONSTRAINT uk_academic_day_academic_year_date
        UNIQUE (academic_year_id, date)
);


CREATE TABLE IF NOT EXISTS academic_classes (
    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,

    name VARCHAR(50) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_academic_classes_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
);


CREATE TABLE IF NOT EXISTS class_sections (
    id UUID PRIMARY KEY,

    teacher_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    academic_class_id UUID NOT NULL,
    academic_year_id UUID NOT NULL,

    name VARCHAR(50) NOT NULL,
    room INTEGER NOT NULL,
    capacity INTEGER NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_class_sections_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES teachers(id),

    CONSTRAINT fk_class_sections_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_class_sections_academic_class
        FOREIGN KEY (academic_class_id)
        REFERENCES academic_classes(id),

    CONSTRAINT fk_class_sections_academic_year
        FOREIGN KEY (academic_year_id)
        REFERENCES academic_years(id),

    CONSTRAINT class_section_id_teacher
        UNIQUE (id, teacher_id)
);


CREATE TABLE IF NOT EXISTS enrollments (
    id UUID PRIMARY KEY,

    organization_id UUID NOT NULL,
    class_section_id UUID NOT NULL,
    academic_year_id UUID NOT NULL,
    student_id UUID NOT NULL,

    roll_no INTEGER NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_enrollments_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_enrollments_class_section
        FOREIGN KEY (class_section_id)
        REFERENCES class_sections(id),

    CONSTRAINT fk_enrollments_academic_year
        FOREIGN KEY (academic_year_id)
        REFERENCES academic_years(id),

    CONSTRAINT fk_enrollments_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT enrollment_academic_year_student
        UNIQUE (academic_year_id, student_id),

    CONSTRAINT enrollment_class_section_roll_no
        UNIQUE (class_section_id, roll_no)
);
