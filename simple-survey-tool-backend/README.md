```bash
curl -X POST http://localhost:8080/api/surveys/ \
-H  'Content-Type: application/json' \
-d '{"questions":["question1?","question2?","question3?"]}'
```

```bash
curl  http://localhost:8080/api/surveys/9
```

```bash
curl -X POST http://localhost:8080/api/surveys/ \
-H  'Content-Type: application/json' \
-d '{"description": "This is a simple survey. Please answer the questions and submit", "questions":["question1?","question2?","question3?"]}'
```


Generate a new survey as follows. 
If no responseOptions are given, the likert scale will be added as responseOptions.
```bash
curl -X POST http://localhost:8081/api/surveys/ \
  -H 'Content-Type: application/json' \
  -d '{
    "description": "This is a simple survey. Please answer the questions and submit",
    "questions": [
      { "text": "Team members appreciate one another´s unique capabilities." },
      { "text": "We are able to resolve conflicts with other teams collaboratively." },
      { "text": "Working on our team inspires people to do their best." },
      { "text": "Team problem solving results in effective solutions." },
      { "text": "Group meetings are very productive." }
    ]
  }'
```


```bash
curl -X POST http://localhost:8080/api/surveys/submit \
  -H 'Content-Type: application/json' \
  -d '{"surveyId":"94e952fb-1106-4bef-8b3e-3b7e20a433c3","answers":{"28c1a0b2-8db9-49ba-bc4c-1e94faf6e004":3,"e8db7700-8b93-4092-ac0b-a291c27706f7":2,"e07f3b8f-bced-453b-9668-344938e983e5":2}}'
```

```bash
curl http://localhost:8080/api/surveys/94e952fb-1106-4bef-8b3e-3b7e20a433c3/results
```



docker compose exec survey-frontend sh
curl http://survey-backend:8080/api/surveys/edcd6ac7-95ff-4d25-b7d8-35a98eedc69c
