# Healthcheck usages
- Restart
- Traffic shaping
- Alerting
- Scaling?
- Deployments?

# Conflicting patterns
Retries?
- Not necessary, healtcheck can retry itself if you give it a lease period.

# Required patterns
Timeouts
- Either in application or at LB level

# Patterns
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
+ Helps if application has: memory, thread leak
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
+ Might help if timeout pattern is not applied.
+ Might help in case of deadlocks, if we probe the system with synthetic requests.

*Traffic shaping*
- _"If the DB server has become a single point of failure (SPOF) and has gone down, there may be an overreaction that can take all of the servers down, depending on how the back-end server check program is written."_

*Alerting*

## Passive healthcheck
*Restart*

*Traffic shaping*

*Alerting*

# Technical Notes
Traefik does not provide TCP healthchecks?
Traefik does not provide health and service status metrics.

# References
https://docs.traefik.io/getting-started/quick-start/
https://landscape.cncf.io/category=service-proxy&format=card-mode&grouping=category&license=open-source&sort=stars
