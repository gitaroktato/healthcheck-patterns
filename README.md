# An overview of health check patterns
Various health check pattern implementations in a sandbox.

# Requirements
You need to install the following
- docker
- minikube
- [bzt](https://gettaurus.org/docs/Installation/)

# Running the examples
To build the project and start up the compose files you have to execute the following command:
```
make docker compose
```
You'll have Grafana with prebuilt dashboards on http://DOCKER_HOST:3000

To generate some load on the services, run the following:
```
make e2e
```
In the `Makefile` `BASE_URL` variable defines the endpoint for testing the application.

To start & stop containers you'll need to use the script in `chaos-testing/kill-container.sh`. It will look at containers flagged as `killable` and remove them from time-to-time.

## Running with Kubernetes
`make k8s-monitoring-deploy` will deploy Prometheus in a `monitoring` namespace.
`make k8s-deploy` will run the application with the dependencies under `test` namespace.

To generate some load on the services, run the following:
```
make e2e
```
You have to set the `BASE_URL` in the `Makefile` to te Kubernetes service endpoint first.

To start & stop pods you'll need to use the script in `chaos-testing/whack-a-pod.sh`. It will look at pods flagged as `killable` and remove them from time-to-time.

# Interacting with the application
You can use the following endpoints for interacting with the deployed application.
`HOST:PORT/application/hello` - will return static response
`HOST:PORT/application/hello` - will increment a counter in the deployed MongoDB database and change custom metrics
`HOST:PORT/application/metrics` - shows metrics endpoint

# Cleanup
Remove deployments from `test` namespace and remove containers from compose files
```
make k8s-clean
make clean
```
