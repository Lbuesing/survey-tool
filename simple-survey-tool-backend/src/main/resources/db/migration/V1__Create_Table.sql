
CREATE TABLE if not exists survey (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(255),
    created_at timestamp,
    updated_at timestamp,
    response_count integer
);
CREATE TABLE if not exists questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    text VARCHAR(255) NOT NULL,
    survey_id UUID NOT NULL REFERENCES survey(id),
    response_options TEXT NOT NULL
);
CREATE INDEX idx_question_survey_id ON questions (survey_id);

