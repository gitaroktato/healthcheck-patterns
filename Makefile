APPLICATION_BASEDIR = application
DOCKER_BASEDIR = $(APPLICATION_BASEDIR)/src/main/docker
DOCKER_IMAGE_VERSION = 1.0
K8S_BASEDIR = $(APPLICATION_BASEDIR)/src/main/kubernetes
# Values are: hello-test, mongo-test, thread-leak-test
SCENARIO = mongo-test
# Envoy endpoint is 192.168.99.100:10000
# Traefik endpoint is 192.168.99.100
BASE_URL = 127.0.0.1:10000
DOCKER = wsl docker
KUBECTL = wsl minikube kubectl

TAURUS_COMMAND = bzt \
	-o settings.artifacts-dir=e2e/logs \
	-o settings.env.BASE_URL=$(BASE_URL) \
	-o execution.0.scenario=$(SCENARIO) e2e/hello-test.yml

.PHONY: e2e

build: docker compose

maven:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./mvnw package"

maven-clean:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./mvnw clean"

dev:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./mvnw quarkus:dev"

docker: maven
	$(DOCKER) build -f $(DOCKER_BASEDIR)/Dockerfile.jvm -t quarkus/application-jvm:$(DOCKER_IMAGE_VERSION) $(APPLICATION_BASEDIR);

compose:
	$(DOCKER) compose -f $(DOCKER_BASEDIR)/load-balancer.yml up -d --force-recreate --build; \
	$(DOCKER) compose -f $(DOCKER_BASEDIR)/monitoring.yml up -d --force-recreate

clean: maven-clean
	$(DOCKER) compose -f $(DOCKER_BASEDIR)/load-balancer.yml down --remove-orphans; \
	$(DOCKER) compose -f $(DOCKER_BASEDIR)/monitoring.yml down --remove-orphans; \
	rm -rf e2e/logs

k8s-monitoring-deploy: 
	$(KUBECTL) apply -f $(K8S_BASEDIR)/monitoring-namespace.yaml \
		-f $(K8S_BASEDIR)/prometheus-config.yaml \
		-f $(K8S_BASEDIR)/prometheus-deployment.yaml \
		-f $(K8S_BASEDIR)/prometheus-service.yaml;
	@echo "=== Prometheus is running at ==="
	minikube service -n monitoring prometheus --url

k8s-deploy:
	$(KUBECTL) apply -f $(K8S_BASEDIR)/test-namespace.yaml \
		-f $(K8S_BASEDIR)/application.yaml \
		-f $(K8S_BASEDIR)/mongo.yaml -n test;
	@echo "=== Application is running at ==="
	minikube service -n test application --url

k8s-rollback:
	$(KUBECTL) rollout undo deployment.v1.apps/application -n test

k8s-clean:
	$(KUBECTL) delete --all -n test po,rs,svc,deploy

e2e:
	$(TAURUS_COMMAND)

restart: clean default
