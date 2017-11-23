# Guidelines

The Anaxes project provides various guidelines from creating Docker images through to recommended project structures.

## Repositories

## Building

## Testing

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

TODO: Include diagram to demonstrate.

### Image Creation

The Anaxes Shipyard project provides some base images ([Java](https://github.com/Alfresco/alfresco-docker-base-java), [Tomcat](https://github.com/Alfresco/alfresco-docker-base-tomcat) that should be used wherever possible.

When building images on top ensure the Docker [best practices](https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices) are followed and provide further common base images where appropriate.

The image repository and tag should be defined in a [properties file](https://github.com/Alfresco/alfresco-docker-base-java/blob/master/build.properties) so the versioning of the image is source controlled. It is highly recommended to use the built-in Bamboo Docker tasks and variables setup by the Delivery Engineering team. [This](https://bamboo.alfresco.com/bamboo/browse/PS-HWS) build plan can be used as a reference.

In-development images can be pushed to the appropriate registry with every build, released images should only be pushed via the manual release stage.

### Registry

A [decision](../adrs/0002-docker-registry-for-internal-and-protected-images.md) has been made to store all internal and protected images in [Quay.io](https://quay.io). All publicly accessible images will be stored in [Docker Hub](https://hub.docker.com).

Stable, released images will be stored in an "alfresco" namespace, in-development images will be stored in an "alfresco-incubator" namespace.

Access to Quay can be requested via an IT request.

As with code artifacts stored on Nexus, Enterprise customers are entitled to access the released protected images. The customer must first create an account on Quay.io and supply the username. An IT request can then be raised to give the customer access to the "alfresco" namespace.

Credentials for pushing to both Quay and Docker Hub are provided via variables in Bamboo, credentials from individual user accounts should never be used in scripts or build plans.

One huge benefit of using Quay is the security scanning service it provides. After an image is updated it is scanned for known vulnerabilities and a report produced. This report should be monitored regularly and known vulnerabilities addressed as soon as possible.

## Helm Chart
