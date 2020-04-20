# Preparation
[x] Availability monitoring - Traefik
[x] Availability monitoring - Envoy
[x] Availability alerts
[x] Availability alerts - Envoy
[x] Dependent services MongoDB?

# Healthcheck & traffic shaping
[x] Getting traffic to only healthy services
[x] Outlier detection
https://www.envoyproxy.io/docs/envoy/v1.13.1/intro/arch_overview/upstream/outlier#arch-overview-outlier-detection

# Healthcheck alerts
[x] Variable to Prometheus time window
[x] Alerts for passive TCP health!
https://www.envoyproxy.io/docs/envoy/v1.13.1/intro/arch_overview/upstream/health_checking#arch-overview-health-checking

# Health & dependencies
[x] How dependencies affect healthcheck overall
[ ] Healthcheck and retires
[ ] Healthcheck and defaults
[x] Pause / unpause dependent containers
[ ] Disk free space
https://download.eclipse.org/microprofile/microprofile-health-2.1/microprofile-health-spec.html
https://rieckpil.de/whatis-eclipse-microprofile-health/

# Healthcheck & restarts
[x] Liveness and readiness probe
[x] Custom healthcheck with failure rate
[x] Probing with synthetic request
https://kubernetes.io/blog/2020/01/22/kubeinvaders-gamified-chaos-engineering-tool-for-kubernetes/
https://medium.com/faun/how-to-inject-chaos-on-kubernetes-resources-using-litmuschaos-c5ddee457b12

[x] Docker healthchecks?
https://howchoo.com/g/zwjhogrkywe/how-to-add-a-health-check-to-your-docker-container
https://www.katacoda.com/courses/prometheus/docker-metrics
```
echo '{ "metrics-addr" : "127.0.0.1:9323", "experimental" : true }' > /etc/docker/daemon.json
systemctl restart docker
```
The metrics endpoint will be accessible once Docker has begun. You can see the raw metrics 
using 
```
curl localhost:9323/metrics
```

# Research 
[x] Prometheus docs (Katacoda?)
[x] Envoy docs (Katacoda?)
https://www.envoyproxy.io/docs/envoy/v1.13.1/start/sandboxes/front_proxy#running-the-sandbox
[x] Watch patterns resiliency
[x] Insert Envoy with metrics for better observability

# Ideation
[x] Full patterns map
[x] All the failure types

# LHF
[ ] e2e from Docker container for better portability
[x] Makefile more variables
[x] Rename service project
[x] Build image first then start app

# Demo
[ ] split between killing containers and starting e2e
