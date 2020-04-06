DOCKER_BASEDIR = getting-started/src/main/docker
# Values are: hello-test, mongo-test
SCENARIO = mongo-test
# Envoy endpoint is 192.168.99.100:10000
# Traefik endpoint is 192.168.99.100
BASE_URL = 192.168.99.100:10000

TAURUS_COMMAND = bzt \
	-o settings.artifacts-dir=e2e/logs \
	-o settings.env.BASE_URL=$(BASE_URL) \
	-o execution.0.scenario=$(SCENARIO) e2e/hello-test.yml

.PHONY: e2e

default: build compose

maven:
	bash -c	"cd getting-started && ./mvnw package"

dev:
	bash -c	"cd getting-started && ./mvnw quarkus:dev"

build: maven
	docker build -f $(DOCKER_BASEDIR)/Dockerfile.jvm -t quarkus/getting-started-jvm getting-started

compose:
	docker-compose -f $(DOCKER_BASEDIR)/load-balancer.yml up -d --force --build; \
	docker-compose -f $(DOCKER_BASEDIR)/monitoring.yml up -d --force

clean:
	docker-compose -f $(DOCKER_BASEDIR)/load-balancer.yml down --remove-orphans; \
	docker-compose -f $(DOCKER_BASEDIR)/monitoring.yml down --remove-orphans; \
	rm -rf e2e/logs

e2e:
	$(TAURUS_COMMAND)

chaos:
	bash -c	"docker ps -q --filter label=killable | xargs chaos-testing/kill-container.sh &"; \
	$(TAURUS_COMMAND)

restart: clean default
