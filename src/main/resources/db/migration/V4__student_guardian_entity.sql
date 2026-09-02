CREATE TABLE IF NOT EXISTS guardians (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,

    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_no VARCHAR(30) NOT NULL,
    relation VARCHAR(50) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_guardian_org_email
        UNIQUE (organization_id, email),

    CONSTRAINT uk_guardian_org_phone
        UNIQUE (organization_id, phone_no),

    CONSTRAINT fk_guardian_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
);

CREATE TABLE IF NOT EXISTS student_guardians (
    student_id UUID NOT NULL,
    guardian_id UUID NOT NULL,

    PRIMARY KEY (student_id, guardian_id),

    CONSTRAINT fk_student_guardians_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student_guardians_guardian
        FOREIGN KEY (guardian_id)
        REFERENCES guardians(id)
        ON DELETE CASCADE
);
