# 8. Using one database per service

Date: 2018-01-10

## Status

Proposed

## Context

Many of the components the digital business platform offers need a database for storing information.
Currently Content Services, Process Services and Sync Service do this but there might be more to come in the future. All three are using the same version of the database so the obvious question was raised: Can we just use one database for all our services?

We identified advantages of using one database per service or one for all the services
Choosing one database per service would put us in line with the current best practices in the microservices world, as well as allowing us to change versions of the db individually, and as an added bennefit it kills off the posibility of one service being able to impact performance on other services.

If we would choose one database for all our components however we would have less components to configure, keep track of, monitor and backup.

## Decision

We will proceed with the one database per service for existing and future components as there are a number of advantages over having all our services fight over the same database.

## Consequences

With each new component that has the need for database storage we would automatically have one more configuration to manage, monitor and backup.
