# Project directories
BACKEND_DIR=simple-survey-tool-backend

RUN_POSTGRESQ:
	docker compose -f docker-compose.yaml up db

BUILD:
	docker run --rm \
		-v $(PWD)/$(BACKEND_DIR):/app \
		-w /app \
		gradle:8.13-jdk17 \
		gradle clean build -x test
	docker compose -f docker-compose.yaml build
RUN:
	docker compose -f docker-compose.yaml up -d
STOP:
	docker compose -f docker-compose.yaml down

