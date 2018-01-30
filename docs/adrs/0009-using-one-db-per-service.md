# 9. Using one database per service

Date: 2018-01-10

## Status

Accepted

## Context

Many of the components the Digital Business Platform offers need a database for storing information.
Currently Content Services, Process Services and Sync Service do this but there might be more to come in the future. All three are using the same version of the database so the obvious question was raised: Can we just use one database for all our services?


We identified several advantages of using one database per service over one for all the services.

Choosing one database per service would put us in line with the current best practices in the microservices world, as well as allowing us to change versions of the DB individually, and as an added benefit it kills off the possibility of one service being able to impact performance on other services.

If we would choose one database for all our components however we would have fewer components to configure, keep track of, monitor and backup.

## Decision

We will proceed with the one database per service for existing and future components as there are a number of advantages over having all our services fight over the same database.

## Consequences

With each new component that has the need for database storage, we would automatically have one more configuration to manage, monitor and backup.
