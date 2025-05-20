# Simple-Survey-Tool
 A simple survey system using Spring Boot, Vue.js, and PostgreSQL



# local execution (no docker):
Start DB:
```bash
make RUN_POSTGRESQ
```
In a separate terminal:
```bash
cd ./simple-survey-tool-backend
export CORS_ALLOWED_ORIGINS=http://localhost:8081,http://survey-frontend:8082
sudo ./gradlew build -x test
java -jar ./build/libs/simple_survey_tool-0.0.1-SNAPSHOT.jar
```
In another separate terminal:
```bash
cd ./simple-survey-tool-frontend
npm run serve
```


# Docker execution
Adjust CORS_ALLOWED_ORIGINS in [docker-compose.yaml](./docker-compose.yaml) to contain the host url

Build:
```bash
make BUILD
```

Run:
```bash
make RUN
```



# Add survey via curl:
Replace \<service-address> with the url of the web service.

```bash
curl -X POST <service-address>/api/surveys/ \
  -H 'Content-Type: application/json' \
  -d '{
    "description": "This is a simple survey about teamwork. Please answer the questions and submit",
    "questions": [
      { "text": "Team members appreciate one another´s unique capabilities." },
      { "text": "We are able to resolve conflicts with other teams collaboratively." },
      { "text": "Working on our team inspires people to do their best." },
      { "text": "Team problem solving results in effective solutions." },
      { "text": "Group meetings are very productive." }
    ]
  }'
```
In the response object, the survey id is shown ('224296de-6e5e-4366-8cbb-803f81b0ace9' in this case):
```
{"id":"224296de-6e5e-4366-8cbb-803f81b0ace9","questions":[{"id":"41ce59da-950d-4012-9bb6-603158710d3d","survey_id":"224296de-6e5e-4366-8cbb-803f81b0ace9","text":"Team members appreciate one another´s unique capabilities.","responseOptions":["Totally disagree","Disagree","Neutral","Agree","Fully Agree"]},{"id":"da0c5e37-2999-436b-8ae4-c7152eeb486c","survey_id":"224296de-6e5e-4366-8cbb-803f81b0ace9","text":"We are able to resolve conflicts with other teams collaboratively.","responseOptions":["Totally disagree","Disagree","Neutral","Agree","Fully Agree"]},{"id":"cb930299-ff08-4f9d-9ea9-28db345513e2","survey_id":"224296de-6e5e-4366-8cbb-803f81b0ace9","text":"Working on our team inspires people to do their best.","responseOptions":["Totally disagree","Disagree","Neutral","Agree","Fully Agree"]},{"id":"c502002a-25ff-40fe-b38f-789f68c3ad5f","survey_id":"224296de-6e5e-4366-8cbb-803f81b0ace9","text":"Team problem solving results in effective solutions.","responseOptions":["Totally disagree","Disagree","Neutral","Agree","Fully Agree"]},{"id":"14458d6c-354c-427d-b3ce-2e890ca63abe","survey_id":"224296de-6e5e-4366-8cbb-803f81b0ace9","text":"Group meetings are very productive.","responseOptions":["Totally disagree","Disagree","Neutral","Agree","Fully Agree"]}],"created_at":"2025-05-20T22:27:24.285293083","updated_at":"2025-05-20T22:27:24.285482717","response_count":0,"description":"This is a simple survey about teamwork. Please answer the questions and submit"}
```

It is also possible to provide custom response options. If no response options are given, the likert scale will be added as response options list. An example question would look like this:
```bash
'{ "text": "Would you like to have more teamwork", "responseOptions": ["yes", "no"]}'
```



# Access your survey at
\<service-address>/surveys/[id]


#
# Access your surveys results at  
\<service-address>/api/surveys/[id]/results

