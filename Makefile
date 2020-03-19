.PHONY: e2e

all: maven compose

maven:
	bash -c	"cd getting-started && ./mvnw package"

compose:
	docker-compose -f getting-started/src/main/docker/load-balancer.yml up -d --build; \
	docker-compose -f getting-started/src/main/docker/monitoring.yml up -d

clean:
	docker-compose -f getting-started/src/main/docker/load-balancer.yml down --remove-orphans; \
	docker-compose -f getting-started/src/main/docker/monitoring.yml down --remove-orphans; \
	rm -rf e2e/logs

e2e:
	bzt -o settings.artifacts-dir=e2e/logs e2e/hello-test.yml

chaos-test:
	bash -c	"docker ps -q --filter name=application | xargs chaos-testing/kill-container.sh &"; \
	bzt -o settings.artifacts-dir=e2e/logs e2e/hello-test.yml
