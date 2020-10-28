# Deep dive into health checks - cloud solutions and beyond

If you're a Java developer, you're most probably working on a back-end application that's being accessed through the network. How do you define if the application is up-and-running?
One of the options is using health checks.

Many frameworks allow default health check implementation, but that doesn't mean we should use them. Do you know when a custom implementation is superior to the framework default? If not, this talk is for you.  I'm going to go through all kinds of health check patterns and explain the differences between them.


## Short description
Explaining health check patterns used in cloud solutions and in modern frameworks. We will use Quarkus, Envoy, Docker, K8s and Traefik.