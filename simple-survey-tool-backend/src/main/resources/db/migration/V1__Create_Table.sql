
CREATE TABLE if not exists surveys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(255),
    created_at timestamp,
    updated_at timestamp,
    response_count integer
);
CREATE TABLE if not exists questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    text VARCHAR(255) NOT NULL,
    survey_id UUID NOT NULL REFERENCES surveys(id),
    response_options TEXT NOT NULL
);
CREATE INDEX idx_question_survey_id ON questions (survey_id);


CREATE TABLE if not exists submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    survey_id UUID NOT NULL REFERENCES surveys(id),
    submitted_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE if not exists answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    submission_id UUID NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES questions(id),
    answer TEXT NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);