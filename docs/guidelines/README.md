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
* All images whose purpose is to be the starting point for other images should contain "base" in their name

### Versioning

We will follow the approach adopted by the Docker community and provide a very specific version/tag that reflects the version of the artifact(s) being deployed as well as a set of "alias" versions for those that always want to use the most current version.

Occasionally an image update is required to fix a bug in the Dockerfile or to fix a vulnerability in a package included in the image. In this scenario the approach the Docker community follows will be used, wherein the image gets updated with the same version/tag. Every time a released image is updated a unique digest is created, the shortened version of the digest must be used to create an additional tag of the image for those consumers that wish to rely on an exact image and avoid the “floating” version issue.

To demonstrate with an example, the Content Services image may choose to use the following tags:

* alfresco-content-services:5.2.2.1
* alfresco-content-services:5.2.2.1-a719933fb4df
* alfresco-content-services:5.2.2
* alfresco-content-services:5.2

It will be left to individual teams to decide how many alias versions they want to provide.

If multiple “flavors” of an image are to provided i.e. based on a different OS, the tag should include the version of each relevant artifact, following the Docker top-down format: ```<version>-<optional flavor>-<dependency>-<dependency flavor/version>....<dependency>-<dependency flavor/version>``` where .... represents each “layer”, the official [OpenJDK](https://hub.docker.com/_/openjdk) and [Tomcat](https://hub.docker.com/_/tomcat) images provide some good examples.

The tag to be used will be specified in a properties file and a script will be used to apply the tag.

To prevent overwrites an image tag will be updated when a new product version or feature/story branch is created. During the development phase an image will use a "-SNAPSHOT" suffix on the product branch and a "-JIRA-NUMBER" suffix on feature/story branches. The suffix will be removed prior to creating a pull/merge request.

Bamboo builds will push the in-development images on every build.

A manual release stage will be provided in the build plan to push non-SNAPSHOT versions. An image tag and all it's aliases will be updated during release. This will also tag the GitHub repository with the tag containing the digest so that consumers can examine the exact state of a given image, if desired.

### Image Creation

The Anaxes Shipyard project provides some base images ([Java](https://github.com/Alfresco/alfresco-docker-base-java), [Tomcat](https://github.com/Alfresco/alfresco-docker-base-tomcat)) that should be used wherever possible.

When building images on top ensure the Docker [best practices](https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices) are followed and provide further common base images where appropriate.

The image repository and tag should be defined in a [properties file](https://github.com/Alfresco/alfresco-docker-base-java/blob/master/build.properties) so the versioning of the image is source controlled. [This](https://bamboo.alfresco.com/bamboo/browse/PS-HWS) build plan can be used as a reference.

In-development images can be pushed to the appropriate registry with every build, released images should only be pushed via the manual release stage.

### Registry

A [decision](../adrs/0002-docker-registry-for-internal-and-protected-images.md) has been made to store all internal and protected images in [Quay.io](https://quay.io). All publicly accessible images will be stored in [Docker Hub](https://hub.docker.com).

Access to Quay can be requested via a BDE ticket.

As with code artifacts stored on Nexus, Enterprise customers are entitled to access the released protected images. The customer must first create an account on Quay.io and supply the username. Granting customer access to Quay can also be requested via a BDE ticket but it should be noted that inviting a customer to a team adds them to the Alfresco organization, inviting them to an individual repository is therefore recommended.

Credentials for pushing to both Quay and Docker Hub are provided via variables in Bamboo, credentials from individual user accounts should never be used in scripts or build plans.

One huge benefit of using Quay is the security scanning service it provides. After an image is updated it is scanned for known vulnerabilities and a report produced. This report should be monitored regularly and known vulnerabilities addressed as soon as possible.

## ADF Applications

For deploying ADF Applications into production it is highly recommended to perform a distribution build and generate an archive, ideally a tar file, for packaging within a Docker container based from an HTTP server image, such as [nginx](https://hub.docker.com/_/nginx/). An example of this approach can be found [here](https://github.com/Alfresco/alfresco-anaxes-hello-world-ui-deployment).

As well as size and performance benefits this approach avoids the [node](https://hub.docker.com/r/_/node/) Docker image which at the time of writing has a number of un-fixable security vulnerabilities.

The use of the Angular proxy feature is also discouraged as this prevents the back-end from being scaled independently from the front-end.

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