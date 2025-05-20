RUN_POSTGRESQ:
	docker compose -f docker-compose.yaml up db

BUILD:
	docker compose -f docker-compose.yaml build
RUN:
	docker compose -f docker-compose.yaml up


BUILD_ARM:
	docker buildx build --platform linux/arm64  ./simple-survey-tool-backend/ --load
	docker buildx build --platform linux/arm64  ./simple-survey-tool-frontend/ --load
	docker save -o survey-backend.tar survey-tool-survey-backend:latest
	docker save -o survey-frontend.tar survey-tool-survey-frontend:latest

LOAD:
	docker load -i survey-backend.tar
	docker load -i survey-frontend.tar 