# 7. Naming and Versioning

Date: 2017-11-17

## Status

Accepted

## Context

To ensure we use a consistent naming and versioning scheme for docker images and helm charts across the company we need to produce a set of guidelines that teams should follow when creating their deployment artifacts.

## Decision

The recommendations made in the [![report](https://img.shields.io/badge/report-PRIVATE-red.svg)](https://ts.alfresco.com/share/s/K9xN7IxnRsuQqyb5QqF2Bw) have been summarized below and formally stated in the [official guidelines](../guidelines/README.md).

### Docker

We will follow the [standard rules](https://docs.docker.com/engine/reference/commandline/tag/#extended-description) for image names with the following additions:

* All Alfresco image names must be prefixed with "alfresco-"
* All Activiti image names must be prefixed with "activiti-"
* All images whose purpose is to be the starting point for other images should contain "base" in their name

We will follow the approach adopted by the Docker community and provide a very specific version/tag that reflects the version of the artifact(s) being deployed as well as a set of "alias" versions for those that always want to use the most current version.

In addition, we will also provide a version suffixed by the shortened digest of the image to allow consumers to use a static image that is guaranteed to not change.

To demonstrate with an example, the Content Services image may choose to use the following tags:

* alfresco-content-services:5.2.2.1
* alfresco-content-services:5.2.2.1-a719933fb4df
* alfresco-content-services:5.2.2
* alfresco-content-services:5.2

If multiple “flavors” of an image are to provided i.e. based on a different OS, the tag should include the version of each relevant artifact, following the top-down format the Docker community uses: ```<artifact-version>-<optional flavor>-<dependency>-<dependency flavor/version>....<dependency>-<dependency flavor/version>```
where .... represents each “layer”, the official [OpenJDK](https://hub.docker.com/_/openjdk/) and [Tomcat](https://hub.docker.com/_/tomcat/) images provide some good examples.

The tag to be used will be specified in a properties file and a script will be used to apply the tag.

To prevent overwrites an image tag will be updated when a new product version or feature/story branch is created. During the development phase an image will use a "-SNAPSHOT" suffix on the product branch and a "-JIRA-NUMBER" suffix on feature/story branches. The suffix will be removed prior to creating a pull/merge request.

Bamboo builds will push the in-development images on every build.

A manual release stage will be provided in the build plan to push non-SNAPSHOT versions. This will also tag the GitHub repository with the same label. An image tag and all it's aliases will be updated during release.

### Helm

We will follow the [best practices](https://docs.helm.sh/chart_best_practices/#conventions) for chart names with the following additions:

* All Alfresco helm chart names must be prefixed with "alfresco-"
* All Activiti helm chart names must be prefixed with "activiti-"
* Any charts that supply common prerequisite items, such as ingresses or volumes, must include “infrastructure” in their name
* The word "service" should be avoided in our helm charts as this is a Kubernetes [concept](https://kubernetes.io/docs/concepts/services-networking/service/).

Helm charts already [mandate](https://docs.helm.sh/chart_best_practices/#version-numbers) the use of semantic versioning.

To prevent overwrites the ```version``` property in the Chart.yaml file will be updated when a new product version or feature/story branch is created.

The ```version``` property will also be updated when the referenced image is updated.

A script will be used to publish charts to the appropriate helm repository.

Bamboo builds will push the in-development charts to the test helm repository on every build.

A manual release stage will be provided in the build plan to push the final version of a chart to the production helm repository. This will also tag the GitHub repository with the same version.

### Artifacts

All artifacts produced by the Anaxes Shipyard will be prefixed with "alfresco-anaxes-".

### GitHub Repositories

Deployment artifacts should reside in a separate repository from the product code. The deployment repository name should be the same as the product repository name with a suffix of "-deployment".

## Consequences

As a result of this decision we will have consistently named docker images whose owners are easily determined. The contents of the image are also easily identifiable by the version/tag used. A static version/tag is provided that is guaranteed not to change.

As a result of this decision we will also have consistently named helm charts that follow the semantic versioning scheme.
