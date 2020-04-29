# healthcheck-patterns
Various health-check pattern implementations

# Requirements


# Running the examples
To build the project and start up the compose files you have to execute the following command:
```
make docker compose
```


# Cleanup
Remove deployments from `test` namespace and remove containers from compose files
```
make k8s-clean
make clean
```