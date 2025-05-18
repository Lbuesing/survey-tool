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
curl -X POST http://localhost:8080/api/surveys/ \
  -H 'Content-Type: application/json' \
  -d '{
    "description": "This is a simple survey. Please answer the questions and submit",
    "questions": [
      { "text": "Teamwork is important" },
      { "text": "Teamwork is fun" },
      {
        "text": "Would you like to have more Teamwork",
        "responseOptions": ["yes", "no"]
      }
    ]
  }'
```