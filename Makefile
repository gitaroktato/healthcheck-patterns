APPLICATION_BASEDIR = application
DOCKER_BASEDIR = $(APPLICATION_BASEDIR)/src/main/docker
DOCKER_IMAGE_VERSION = 1.0
K8S_BASEDIR = $(APPLICATION_BASEDIR)/src/main/kubernetes
# Values are: hello-test, mongo-test
SCENARIO = mongo-test
# Envoy endpoint is 192.168.99.100:10000
# Traefik endpoint is 192.168.99.100
BASE_URL = 192.168.99.100:32568

TAURUS_COMMAND = bzt \
	-o settings.artifacts-dir=e2e/logs \
	-o settings.env.BASE_URL=$(BASE_URL) \
	-o execution.0.scenario=$(SCENARIO) e2e/hello-test.yml

.PHONY: e2e

build: docker-build compose

maven:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./mvnw package"

maven-clean:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./mvnw clean"

dev:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./mvnw quarkus:dev"

docker-build: maven
	docker build -f $(DOCKER_BASEDIR)/Dockerfile.jvm -t quarkus/application-jvm:$(DOCKER_IMAGE_VERSION) $(APPLICATION_BASEDIR)

compose:
	docker-compose -f $(DOCKER_BASEDIR)/load-balancer.yml up -d --force --build; \
	docker-compose -f $(DOCKER_BASEDIR)/monitoring.yml up -d --force

clean: maven-clean
	docker-compose -f $(DOCKER_BASEDIR)/load-balancer.yml down --remove-orphans; \
	docker-compose -f $(DOCKER_BASEDIR)/monitoring.yml down --remove-orphans; \
	rm -rf e2e/logs

k8s-monitoring-deploy: 
	kubectl apply -f $(K8S_BASEDIR)/monitoring-namespace.yaml \
		-f $(K8S_BASEDIR)/prometheus-config.yaml \
		-f $(K8S_BASEDIR)/prometheus-deployment.yaml \
		-f $(K8S_BASEDIR)/prometheus-service.yaml;
	@echo "=== Prometheus is running at ==="
	minikube service -n monitoring prometheus --url

k8s-deploy: k8s-monitoring-deploy docker-build
	kubectl apply -f $(K8S_BASEDIR)/test-namespace.yaml \
		-f $(K8S_BASEDIR)/application.yaml \
		-f $(K8S_BASEDIR)/mongo.yaml -n test;
	@echo "=== Application is running at ==="
	minikube service -n test application --url

k8s-rollback:
	 kubectl rollout undo deployment.v1.apps/application -n test

e2e:
	$(TAURUS_COMMAND)

chaos:
	bash -c	"docker ps -q --filter label=killable | xargs chaos-testing/kill-container.sh &"; \
	$(TAURUS_COMMAND)

restart: clean default
