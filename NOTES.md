# Healthcheck usages
- Restart
- Traffic shaping
- Alerting
- Scaling?
- Deployments?

# Service Level Failure Types
 memory, thread leak, pool misconfigurations (*TBD* all types), deadlocks

# Conflicting patterns
Retries?
- Not necessary, healtcheck can retry itself if you give it a lease period.

Fallbacks & Defaults?
- Deep healthcheck might ruin the effect of fallbacks

# Required patterns
Timeouts
- Either in application or at LB level

# Health Check Patterns
## No healthcheck
*Restart*
N/A

*Traffic shaping*
N/A

*Alerting*
+ Based on the ratio of failed HTTP requests indirectly (passive)
+ Works for TCP as well!
- *DELAYING* if the rate drops because a dependent service timing out

## Shallow healthcheck
*Restarts*
*TBD*
+ Helps if application has: memory, thread leak, pool misconfigurations
- Won't help if dependent service becomes unavailable
- Won't help in case of deadlocks

*Traffic shaping*
+ Guarantees that only healthy services will recieve traffic
+ Reduces failure rate becasue of downtime

- Won't help if dependent services are not operational

*Alerting*
+ Based on reported health check status
- Not working if a dependent service is not operational

+ Based on the ratio of failed HTTP requests indirectly (passive)
- *DELAYING* if the rate drops because a dependent service timing out

## Deep healthcheck
*Restart*
*TBD*
+ Might help if timeout pattern is not applied.
+ Might help in case of deadlocks, if we probe the system with synthetic requests.

*Traffic shaping*
*TBD*
+ Removes pressure from services until they become healthy again

- _"If the DB server has become a single point of failure (SPOF) and has gone down, there may be an overreaction that can take all of the servers down, depending on how the back-end server check program is written."_
- Can ruin the effect of fallbacks and defaults

*Alerting*
+ Better understanding on higher level from application perspective
+ Overview on current state even if network partitioning occurs

## Passive healthcheck
*Restart*
*TBD*

*Traffic shaping*
+ Removes pressure from services until they become healthy again
+ Don't have to sync your configuration with fallbacks, defaults, circuit-breakers

*Alerting*
+ Better understanding on higher level from application perspective
+ Overview on current state even if network partitioning occurs

# Technical Notes
Traefik does not provide TCP healthchecks?
Traefik does not provide health and service status metrics.

# References
https://docs.traefik.io/getting-started/quick-start/
https://landscape.cncf.io/category=service-proxy&format=card-mode&grouping=category&license=open-source&sort=stars
