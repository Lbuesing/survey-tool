RUN_POSTGRESQ:
	docker compose -f docker-compose.yaml up db

BUILD:
	docker compose -f docker-compose.yaml build
RUN:
	docker compose -f docker-compose.yaml up