# 1. helm chart repository

Date: 2017-11-13

## Status

Proposed

## Context

As part of our efforts around containerized deployment of the Alfresco Digital Business Platform we need to decide on a Helm Chart Repository that Alfresco engineering teams, other internal groups in the organization, and customers and partners can use to publish and consume Helm Charts. When we looked for a solution, we considered that this is going to be a public-facing customer repository. We also took into consideration the need for a custom domain for it.

When we considered each option, we looked: 
* if it uses AWS
* if it uses Quay
* if it is a PaaS/Cloud Storage solution
* if the project is mature enough for our use case
* if it is a personal project or if it is maintained by a company
* if it requires IT support (if it does can it be avoided by altering the pipeline)

We had several options that we considered. The full list of pros and cons for each option can be found [here](https://issues.alfresco.com/jira/secure/attachment/97743/DEPLOY-150%20Helm%20Chart%20Repos.xlsx). More comments on the investigation we have done can be found [here](https://issues.alfresco.com/jira/browse/DEPLOY-150).

## Decision

We decided that the most apropriate solution for our use case is Github Pages. We choosed it because it matches the requirements and it offers an easy, well-known pipeline.

## Consequences

Using the official charts layout as a guide, create a repository called charts at https://github.com/Alfresco/charts. We will use the master branch. Also we will have a custom domain: kuberenetes-charts.alfresco.com. We will have stable and incubator as top-level folders and update the Anaxes Shipyard guidelines to describe how to use the Helm Repository. We will also change CI/CD pipeline to be able to push and pull charts to/from the Helm Repository.
