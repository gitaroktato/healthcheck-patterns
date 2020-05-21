# An overview of health check patterns

Many developers have some existing health check mechansim implemented especially nowadays in the "microservices era" of backend development. I really hope that you also do. Whenever you have something simple that just throws a HTTP 200 back at the caller or a more complex logic, it's good to be aware of the pros & cons of different health check implementations. In this article I'm going to go through each type of health checks and investiagate what kind of  issues can be resolved with each of them.

# Why do we need health checks at all?

Good question! Especially we have to consider how far I can get away with postponing the implementation. The reasons for not having health checks can be various, like tight project deadlines, corporate politics or complex configurations of vendor specific hardware. I won't judge you. But you have to know, that just because your code seem static it doesn't mean that it's behaving the same way when running for a longer period. You're depending on a computer hardware, 3rd party libraries, dependencies manintaned by other teams and none of them are providing 100% guarantees. As a rule of thumb you can't build 100% reliable software on top of unreliable components. Your service is going to fail shortly after your first release to production. And if it does, you have to detect it somehow. We all can agree that it's better to do it before end-users do.

## Redundancy

The simplest way to introduce fault-tolerance into any system is by introducing redundancy. You can make your data redundant by copying them over several times and hiding "bad bytes", like a RAID configuration does with multiple hard drives. Similarly, you can also make a database time redundant, by holding and serving multiple versions of the same data. For services what works best is making them process redundant. Keeping multiple processes running at the same time, so if one of them misbehaves others can take over the workload. Of course this only works if you have some kind-of coordination in place. Usually this is done by using health checks.

# Anatomy of a helath check

The coordination I was talking above can a container orchestrator or a load balancer for example. The role of these coordinators is to hide implementation details from the clients using your cluster of services and show them as a single component. In order to do this, they have to schedule workload to only those services, which are reported to be healthy. They ask each of the running process in the cluster about their health and take an action based on the response. These actions can be various. Some of them are

- Restarts
- Alerting
- Traffic shaping
- Scaling
- Deployments

# Various health check implementations

You can implement health checks in many-many ways and I'm going to show you an example on each. Then we're investigating the typical type of failures they are capable to indicate and their effectiveness.

## About the examples
You can find a sandbox with predefined health check implementations at https://github.com/gitaroktato/healthcheck-patterns. I'm going to use Envoy, Traefik, Prometheus, Grafana, Quarkus and minikube for represeinting the various pattners.

# No health checks
No health checks? No problem! At least your implementation is not misleading. But can we configure at least someting useful for these services as well?

## Restarts
The good news is that you can still rely on your container orchestrator if you've configured your container properly. Kubernetes restarts them if they stop, but it will happen only if your crashed process is also causing its container to exit. 

## Alerts
You can still set up alerts based on the type of HTTP responses your load balancer sees, if you're using L7 load balancing. Unfortunately both with Envoy and Traefik it's not possible in-case of a TCP load balancer, because the lack of interpretation of the HTTP response codes. I used these two PromQL queries and configured an alert if the error rate for a given service got higher than a specified threshold.

```
sum(rate(envoy_cluster_upstream_rq_xx{envoy_response_code_class="5"}[$interval])) / 
sum(rate(envoy_cluster_upstream_rq_xx[$interval]))
```

```
sum(rate(traefik_entrypoint_requests_total{code=~"5.."}[$interval])) / 
sum(rate(traefik_entrypoint_requests_total[$interval]))
```

One thing that's important to mention here, is that in many cases when a dependency (e.g. the database) is not available the response rate of the service drops. This causes spikes in the queries above, which are unseen for the predefined `$interval` because the previous higher rate of successful response. Below you can find two samples from the same time period with two different intervals. You can see that the spike is unseen if the duration of the timeout of the dependent service is less than the predefined interval. You can use it for your advantage to avoid alerts for intermittent outages which are not worth act upon (at-least not in the middle of the night).

# TODO images

## Deployments & Traffic Shaping
I found no options to control deployments and traffic shaping if you don't have a health check implementation in-place. So, you need to advance to the next level of health checks if you plan to improve these two activities.

# Shallow Health Checks
Shallow health checks usually just verify if the HTTP pool is capable of providing some kind-of response. They do this by returning a static content or empty page with an HTTP 2xx response code. In some scenarios it makes sense to do a bit more than that and check the amount of free disk space under the service. If it falls under a predefined threshold, the service can report itself as unhealthy. This provides some additional information in-case there's a need to write to local filesystem (because of logging), but far from being perfect: Checking free disk space is not the same as trying to write to file system. And there's no guarantee that write will succeed. If you're out of i-nodes, your log rotation can still fail and can lead to unwanted consequences. An exameple HTTP response of such implementation can be found [in my code][disk-health].

```
{
    "status": "UP",
    "checks": [
        {
            "name": "disk",
            "status": "UP",
            "data": {
                "usable bytes": 82728624128
            }
        }
    ]
}
```

Spring Boot Actuator has a [default implementation][spring-boot-disk-health] that has similar functionalities. 

```
{
   "status":"UP",
   "details":{
      "diskSpace":{
         "status":"UP",
         "details":{
            "total":250790436864,
            "free":100327518208,
            "threshold":10485760
         }
      }
   }
}
```

## Restarts
In Kubernetes we have the option to configure a [liveness/readiness][liveness-readines] probe for our containers. With the help of this feature our service will be restarted automatically, in case it becomes unhealthy. This will recover some issues with the HTTP pool, like thread leaks and deadlocks and some of the more generic faults i.e. memory leaks. Note, that you won't catch any deadlock that occurs further in the stack, like at the database or integration level.

If we're concerned about I/O operations, including disk free space in our health check can result as respawning our container in another worker node which hopefully has now enough to keep our services running. 

In my sandbox here's the [liveness and readiness probe configuration][liveness-readiness-example] so you can try different scenarios by yourself.

## Traffic shaping
The most common way of using health checks is to integrate them with load balancers, so they can route traffic to only healthy instances. But what should we do in cases, when the database is not accessible from the service's point of view? In this scenario they will still retrieve workload and probably fail when trying to write to the database. We have 3 different options that offer some resolution:

- Try to store the request in the service itself and retry later
- Fail fast and let the caller do the retry
- Include database in the health indicator of the service and try to reduce the number of these cases

The first opiton will lose the request if the service restarts. The second option moves the problem one layer above. The third option leads us to deep health checks.

## Alerting

# Deep health checks

## Traffic shaping

## TODO connection pool issues.
Shoud the service report itself as healthy or unheatlhy? This can indicate at least two different problems: Either the connection pool experiencing hard times or the database has some issues. In the later case most probably other instances will also report themselves as unhealthy... 

## TODO!!! > Health checks are essential!!! Hahaha

# Summary
Health checks are just one aspect of fault tolerance
- Introduce alerts one layer below

## Maturity level
Shallow or deep -> probing -> passive

[liveness-readines]: https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/
[disk-health]: https://github.com/gitaroktato/healthcheck-patterns/blob/master/application/src/main/java/org/acme/quickstart/health/DiskHealthCheck.java
[spring-boot-disk-health]: https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/system/DiskSpaceHealthIndicator.java
[liveness-readiness-example]: https://github.com/gitaroktato/healthcheck-patterns/blob/master/application/src/main/kubernetes/application.yaml#L27