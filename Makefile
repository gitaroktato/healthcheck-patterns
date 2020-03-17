.PHONY: all

all: maven compose compose_monitoring

maven:
	bash -c	"cd getting-started && ./mvnw package"

compose:
	docker-compose -f getting-started/src/main/docker/load-balancer.yml up -d

compose_monitoring:
	docker-compose -f getting-started/src/main/docker/monitoring.yml up -d

clean:
	docker-compose -f getting-started/src/main/docker/load-balancer.yml down; \
	docker-compose -f getting-started/src/main/docker/monitoring.yml down;


