.PHONY: e2e

all: maven compose

maven:
	bash -c	"cd getting-started && ./mvnw package"

compose:
	docker-compose -f getting-started/src/main/docker/load-balancer.yml up -d --build; \
	docker-compose -f getting-started/src/main/docker/monitoring.yml up -d

clean:
	docker-compose -f getting-started/src/main/docker/load-balancer.yml down; \
	docker-compose -f getting-started/src/main/docker/monitoring.yml down; \
	rm -rf e2e/logs

e2e:
	bzt -o settings.artifacts-dir=e2e/logs e2e/hello-test.yml
