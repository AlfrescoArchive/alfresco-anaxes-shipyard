# Guidelines

The Anaxes project provides various guidelines from creating Docker images through to recommended project structures.

## Repositories

There are no further guidelines regarding repositories over and above the [standard policies](https://w3.alfresco.com/confluence/pages/viewpage.action?spaceKey=AEO&title=Code+Storage+Policies+and+Locations).

However, it is highly recommended to manage the deployment artifacts in a separate GitHub repository and suffix the repository name with "-deployment".

For example, the deployment artifacts for the code in "alfresco-dsync-services" should reside in a repository named "alfresco-dsync-services-deployment".

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

### Scripts

The use of inline build scripts should be kept to a minimum (ideally avoided completely), having scripts under source control is best practice and allows developers to run the same actions the build performs.

Hard-coded properties in scripts should also be avoided, externalize them in separate properties/yaml files.

### Publishing

Publishing (releasing) an artifact must be performed by a manual release stage in the build plan. The manual release stage must handle the manipulation of version numbers so that any in-development markers are removed, increment the version, move the artifact to it's stable container and tag the source repository appropriately. Java based projects, for example, will most likely use the [maven release plugin](http://maven.apache.org/maven-release/maven-release-plugin).

Published artifacts must **NOT** have any in-development dependencies.

### Security

Build plans or scripts should **NOT** contain hard-coded credentials, neither should machine-wide credentials i.e. ~/.docker/config.json be used directly or added to Kubernetes clusters.

IT managed credentials are available as Bamboo variables for most internal systems, these should be leveraged where possible, anything else should be stored in a private repository and checked out as part of the job.

## Testing

## Docker

## Helm Chart
