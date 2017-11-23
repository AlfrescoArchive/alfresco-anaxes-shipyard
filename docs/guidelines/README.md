# Guidelines

The Anaxes project provides various guidelines from creating Docker images through to recommended project structures.

## Repositories

## Building

## Testing

## Docker

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