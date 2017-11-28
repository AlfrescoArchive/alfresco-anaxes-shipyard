# 6. Naming and Versioning

Date: 2017-11-17

## Status

Accepted

## Context

To ensure we use a consistent naming and versioning scheme for docker images and helm charts across the company we need to produce a set of guidelines that teams should follow when creating their deployment artifacts.

## Decision

The recommendations made in the [report](https://ts.alfresco.com/share/s/DSA7dX-iSnG97AQkaftb2w) have been summarized below and formally stated in the [official guidelines](../guidelines/README.md).

### Docker

We will follow the [standard rules](https://docs.docker.com/engine/reference/commandline/tag/#extended-description) for image names with the following additions:

* All Alfresco image names must be prefixed with "alfresco-"
* All images provided by the Platform Services team must be prefixed with "alfresco-base-"
* The use of the "alfresco-base-" prefix is reserved exclusively for the Platform Services team

We will follow the approach adopted by the Docker community and provide a very specific version/tag that describes the reflects the version of the artifact(s) being deployed as well as a set of "alias" versions for those that always want to use the most current version.

In addition, we will also provide a version suffixed by the shortened digest of the image to allow consumers to use a static image that is guaranteed not to change.

### Helm

We will follow the [best practices](https://docs.helm.sh/chart_best_practices/#conventions) for chart names with the following additions:

* All Alfresco helm chart names must be prefixed with "alfresco-"
* Any charts that supply common prerequisite items, such as ingresses or volumes, must include “infrastructure” in their name
* The word "service" should be avoided in our helm charts as this is a Kubernetes [concept](https://kubernetes.io/docs/concepts/services-networking/service/).

Helm charts already [mandate](https://docs.helm.sh/chart_best_practices/#version-numbers) the use of semantic versioning.

### Artifacts

All artifacts produced by the Anaxes Shipyard will be prefixed with "alfresco-anaxes-".

### GitHub Repositories

Deployment artifacts should reside in a separate repository from the product code. The deployment repository name should be the same as the product repository name with a suffix of "-deployment".

## Consequences

As a result of this decision we will have consistently named docker images whose owners are easily determined. The contents of the image are also easily identifiable by the version/tag used. A static version/tag is provided that is guaranteed not to change.

As a result of this decision we will also have consistently named helm charts that follow the semantic versioning scheme.