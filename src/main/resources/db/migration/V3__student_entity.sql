CREATE TABLE IF NOT EXISTS students (
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,
    organization_id UUID NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    dob DATE NOT NULL,

    gender VARCHAR(255),
    address VARCHAR(255),
    current_roll_number INTEGER,
    current_class INTEGER,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_users_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_students_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);
