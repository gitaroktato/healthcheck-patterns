DOCKER_BASEDIR = getting-started/src/main/docker
# Values are: hello-test and mongo-test
SCENARIO = mongo-test

.PHONY: e2e

default: compose

maven:
	bash -c	"cd getting-started && ./mvnw package"

dev:
	bash -c	"cd getting-started && ./mvnw quarkus:dev"

build: maven
	docker build -f $(DOCKER_BASEDIR)/Dockerfile.jvm -t quarkus/getting-started-jvm getting-started

compose: build
	docker-compose -f $(DOCKER_BASEDIR)/load-balancer.yml up -d --force --build; \
	docker-compose -f $(DOCKER_BASEDIR)/monitoring.yml up -d --force

clean:
	docker-compose -f $(DOCKER_BASEDIR)/load-balancer.yml down --remove-orphans; \
	docker-compose -f $(DOCKER_BASEDIR)/monitoring.yml down --remove-orphans; \
	rm -rf e2e/logs

e2e:
	bzt -o settings.artifacts-dir=e2e/logs -o execution.0.scenario=$(SCENARIO) e2e/hello-test.yml

chaos:
	bash -c	"docker ps -q --filter label=killable | xargs chaos-testing/kill-container.sh &"; \
	bzt -o settings.artifacts-dir=e2e/logs -o execution.0.scenario=$(SCENARIO) e2e/hello-test.yml

restart: clean default
