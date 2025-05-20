# Project directories
BACKEND_DIR=simple-survey-tool-backend
FRONTEND_DIR=simple-survey-tool-frontend

# Gradle and Docker image settings
JAR_PATH=$(BACKEND_DIR)/build/libs



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
	docker compose -f docker-compose.yaml up


BUILD_ARM:
	docker buildx build --platform linux/arm64 --output type=docker,dest=simple-survey-backend.tar   ./simple-survey-tool-backend/ 
	docker buildx build --platform linux/arm64 --output type=docker,dest=simple-survey-frontend.tar  ./simple-survey-tool-frontend/ 

LOAD:
	docker load -i survey-backend.tar
	docker load -i survey-frontend.tar 