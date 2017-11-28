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

This section provides some guidelines for testing the deployment of Helm charts into Kubernetes clusters.

### Setup Cluster

The current recommendation is to deploy and run a cluster that can be used for testing rather than attempting to deploy a whole cluster during a build plan. The [Running a Cluster](../running-a-cluster.md) and [Tips and Tricks](../tips-and-tricks.md) pages can be used to do this.

Unfortunately Minikube needs to run on dedicated hardware (it uses virtualization itself). We currently have one dedicated MacMini build agent running Minikube, this pool will need to be expanded as usage increases.

To avoid conflicts and make testing easier it's highly recommended to deploy into a new namespace, this should be done as part of the "Deploy & Test" stage of the [build plan](#plans).

### Deployment

To test the deployment of Helm charts the usage of the [Fabric8 Java client](https://github.com/fabric8io/kubernetes-client) is recommended. It provides an API for interacting with Kubernetes clusters and can be used to verify pods were deployed as expected, for a working example examine [this project](https://github.com/Alfresco/alfresco-anaxes-hello-world).

Any Kubernetes specific testing, for example, ensuring an ELB has been provisioned and is accessible, should be done in these tests.

### End-to-End

As well as the usual unit and integration tests, standalone tests designed to take a target URL and execute against a running system should also be provided. This allows the same set of tests to be shared and run against a local HTTP server, a local Kubernetes cluster or a remote, highly available Kubernetes cluster.

## Docker

With Docker still being a fairly new technology standards are scarce, images are therefore created in a variety of ways, the guidelines in this section are to ensure there is consistency across the company.

### Naming

In addition to the Docker [recommendations](https://docs.docker.com/engine/reference/commandline/tag/#extended-description), the following rules also apply:

* All images produced and published by Alfresco must be prefixed with “alfresco-”, for example “alfresco-content-services”
* All images produced and published by the Activiti open source project must be prefixed with “activiti-”, for example “activiti-cloud-runtime-bundle”
* Images intended to be used as a starting point for other images should be prefixed with "alfresco-base-"

### Versioning

In-development images must be identified with a tag ending with "-SNAPSHOT" or "-JIRA-NUMBER" (for story/feature branches) and stored in the "alfresco-incubator" namespace.

The version number should reflect the version of the main artifact(s) the docker image is deploying. For example, our base Java image includes the OS and Java so the version would be similar to “oracle-8u151-centos-7.4”.

Where appropriate “alias” versions should also be provided, for example, if consumers want to always use the latest Java update version they could use the “centos-7.4-oracle-8” version.

Individual teams may choose how many alias versions they wish to provide.

Occasionally an image update is required to fix a bug in the Dockerfile or to fix a vulnerability in a package included in the image. In this scenario the approach the Docker community follows must be used, wherein the image gets updated with the same version/tag.

Every time a released image is updated a unique digest is created, the shortened version of the digest must be used to create an additional tag of the image for those consumers that wish to rely on an exact image and avoid the “floating” version issue. Using the Java base image example above the following tag would also be created “centos-7.4-oracle-8u151-882c834c0fba”.

The repository containing the Dockerfile must also be tagged with the shortened digest so that consumers can examine the exact state of a given image, if desired.

TODO: Include diagram to demonstrate once versioning has been agreed.

### Image Creation

The Anaxes Shipyard project provides some base images ([Java](https://github.com/Alfresco/alfresco-docker-base-java), [Tomcat](https://github.com/Alfresco/alfresco-docker-base-tomcat)) that should be used wherever possible.

When building images on top ensure the Docker [best practices](https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices) are followed and provide further common base images where appropriate.

The image repository and tag should be defined in a [properties file](https://github.com/Alfresco/alfresco-docker-base-java/blob/master/build.properties) so the versioning of the image is source controlled. It is highly recommended to use the built-in Bamboo Docker tasks and variables setup by the Delivery Engineering team. [This](https://bamboo.alfresco.com/bamboo/browse/PS-HWS) build plan can be used as a reference.

In-development images can be pushed to the appropriate registry with every build, released images should only be pushed via the manual release stage.

### Registry

A [decision](../adrs/0002-docker-registry-for-internal-and-protected-images.md) has been made to store all internal and protected images in [Quay.io](https://quay.io). All publicly accessible images will be stored in [Docker Hub](https://hub.docker.com).

Stable, released images will be stored in an "alfresco" namespace, in-development images will be stored in an "alfresco-incubator" namespace.

Access to Quay can be requested via a BDE ticket.

As with code artifacts stored on Nexus, Enterprise customers are entitled to access the released protected images. The customer must first create an account on Quay.io and supply the username. A BDE ticket can then be raised to give the customer access to the "alfresco" namespace.

Credentials for pushing to both Quay and Docker Hub are provided via variables in Bamboo, credentials from individual user accounts should never be used in scripts or build plans.

One huge benefit of using Quay is the security scanning service it provides. After an image is updated it is scanned for known vulnerabilities and a report produced. This report should be monitored regularly and known vulnerabilities addressed as soon as possible.

## Helm Chart

Helm charts are the deployment package customers will use so having consistency across all our charts is critical.

### Naming

As per [Helm best practices](https://docs.helm.sh/chart_best_practices/#conventions), the chart name should only contain lowercase letters, numbers and dashes (-). Furthermore, the name of the folder housing the chart must be the same as the name of the chart within Chart.yaml.

All charts produced and published by Alfresco must be prefixed with “alfresco-”, for example “alfresco-content-services”.

All charts produced and published by the Activiti open source project must be prefixed with “activiti-”, for example, “activiti-cloud-runtime-bundle”.

Any charts that supply prerequisites common to other charts, such as ingresses or volumes, must include “infrastructure” in their name, for example “alfresco-dbp-infrastructure”.

When developing charts the term “service” can quickly become overloaded as it’s also a Kubernetes [concept](https://kubernetes.io/docs/concepts/services-networking/service), an alternative name for the “backend” should be used.

### Versioning

As a chart often deploys multiple products it must define an independent version number using the `version` property in Chart.yaml.

If a chart is only deploying one product, the version of the artifact being deployed should be defined using the `appVersion` property in Chart.yaml.

Version numbers must follow semantic versioning using major.minor.patch i.e. 1.0.1. In fact, this is [mandated](https://docs.helm.sh/chart_best_practices/#version-numbers) by helm.

### Chart Creation

When creating charts the [Helm best practices](https://docs.helm.sh/chart_best_practices/#conventions) must be followed.

New charts must start in the incubator state. Once they have been verified and tested they are promoted, via a manual release stage in a build plan, to the stable state. This is the same approach the Kubernetes community follow.

As a best practice, Charts should define both a readiness and health probe as this helps Kubernetes maintain a healthy system.

### Repository

Like Maven and Docker, Helm can host charts in a repository, this makes dependency handling much simpler.

To support the incubator to stable phases a repository typically has a stable and incubator folder.

At the beginning of a charts life-cycle it should be pushed to an internal test repository and then pushed to the production repository when appropriate via a manual release stage in a build plan.

If desired, a chart can be pushed to the incubator folder of the production repository to gather feedback, for example.