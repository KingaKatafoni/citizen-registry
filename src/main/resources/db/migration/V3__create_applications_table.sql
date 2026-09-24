CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
    application_number VARCHAR(20) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    citizen_id BIGINT NOT NULL REFERENCES citizens(id),
    assigned_officer_id BIGINT REFERENCES officers(id),
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    rejection_reason TEXT,
    notes TEXT
);