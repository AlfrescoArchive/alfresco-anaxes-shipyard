# 7. Using one database per service

Date: 2018-01-10

## Status

Proposed

## Context

Many of the components the digital business platform offers need a database for storing information.
Currently Content Services, Process Services and Sync Service do this but there might be more to come in the future. All three are using the same version of the database so the obvious question arrised: Can we just use one database for all our services?

We identified advantages of using one database per service or one for all the services:
One database per service:
Best practise in microservices world (decoupling)
Allows any service to move to a different version of PostgreSQL or change persistence layer
Stops one service from being able to slow down the DB, impacting performance on other services

One database for all services:
Less components to configure, keep track of, monitor and backup

## Decision

We will proceed with the one database per service for existent and future components as there are a number of advantages over having all our services fight over the same database.

## Consequences

With each new component that has the need for database storage we would automatically have one more configuration to manage, monitor and backup.
