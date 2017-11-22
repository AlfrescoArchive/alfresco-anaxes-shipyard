# Guidelines

The Anaxes project provides various guidelines from creating Docker images through to recommended project structures.

## Repositories

## Building

In order to have a consistent, repeatable, audited process every artifact must be built and published by a build plan, ideally in [Bamboo](https://bamboo.alfresco.com).

### Plans

Ideally there should be separate build plans for the code artifact, the Docker image and the Helm chart. However, due to a limitation in Bamboo, branch plans can not be triggered by other branch plans. Until this is resolved the recommendation is to use the following stages in a single plan:

* Build & Test
* Build Docker Image
* Build Helm Chart
* Deploy & Test

The diagram below shows this in pictorial form and The [Hello World Service build plan](https://bamboo.alfresco.com/bamboo/browse/PS-HWS) can be used as an illustrative example.

![Build Plan Structure](./diagrams/build-plan.png)

### Publishing

Publishing (releasing) an artifact must be performed by a manual release stage in the build plan, an example of this can be found [here](#TODO).

Published artifacts must **NOT** be dependent on any SNAPSHOT versions.

### Security

Build plans or scripts should **NOT** contain hard-coded credentials, neither should machine-wide credentials i.e. ~/.docker/config.json be used directly or added to Kubernetes clusters.

IT managed credentials are available as Bamboo variables for most internal systems, these should be leveraged where possible, anything else should be stored in a private repository and checked out as part of the job.

## Testing

## Docker

## Helm Chart
