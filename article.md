# An overview on health check patterns

Many developers have some existing health check mechansim implemented especially nowadays in the "microservices era" of backend development. I really hope that you also do. Whenever you have something simple that just throws a HTTP 200 back at the caller or a more complex logic, it's good to be aware of the pros & cons of different health check implementations. In this article I'm going to go through each type of health checks and investiagate what kind of  issues can be resolved with each of them.

# Why do we need health checks at all?

Good question! Especially we have to consider how far I can get away with postponing the implementation. The reasons for not having health checks can be various, like tight project deadlines, corporate politics, complex configurations of vendor specific hardware. I won't judge you. But you have to know, that just because your code seem static it doesn't mean that it's behaving the same way when running for a longer period. You're depending on a computer hardware, 3rd party libraries, dependencies manintaned by other teams and none of them are providing 100% guarantee. As a rule of thumb you can't build 100% reliable software on top of unreliable components. Your service is going to fail shortly after your first release to production. And if it does, you have to detect it somehow. We all can agree that it's better to do it before end-users do.

## Redundancy

The simplest way to introduce fault-tolerance into any system is by introducing redundancy. You can make your data redundant by copying them over several times and hiding "bad bytes", like a RAID configuration does with multiple hard drives. Similarly, you can also make a database 

# Anatomy of a helath check



# 
