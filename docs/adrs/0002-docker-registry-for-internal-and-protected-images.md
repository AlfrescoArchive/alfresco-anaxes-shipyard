# 2. Docker Registry for Internal and Protected Images

Date: 2017-09-29

## Status

Accepted

## Context

As part of our efforts around containerized deployment of the Alfresco Digital Business Platform we need to standardize on a Docker Image Registry that Alfresco engineering teams, other internal groups in the organization, and customers and partners can use to publish and consume Docker images.  We can describe those as 'internal' and 'protected' tiers.

The Nexus3 implementation in use at the time of writing does not meet our requirements around access control, security scanning, scalability and global performance, usability, or maintainability.

Our IT resources are currently stretched very thin and we should avoid adding another system for them to deploy and maintain if possible.

## Decision

We will use [Quay.io](https://quay.io) for the internal and protected tiers of access and use Docker Hub for public repositories (images of community versions and/or enterprise artifacts with trail licenses).

We’d like to limit the introduction of additional deployments (particularly customer-facing) that our IT staff has to maintain, so we'd prefer a SaaS solution.

The REST API of Quay.io allows our organization to potentially automate user provisioning/invitation and user/group management which is not available for Docker Cloud at this time.

Additionally, Quay / CoreOS seems strongly committed to their SaaS offering while Docker seems entirely focused on their Enterprise ‘on-prem’ product.

The summary [![report of the comparison](https://img.shields.io/badge/report%20of%20the%20comparison-PRIVATE-red.svg)](https://ts.alfresco.com/share/s/mVAV1sGIReC_iqgMN0GGnQ) also contains reference links to the full investigation.

## Consequences

### Cost
Quay's pricing is per repository and we estimate the initial expenditure will be $100 / month, increasing over time with the increased adoption of Docker within our organization.

### Managing Access Control
Processes should be defined for requesting that customers create an account at the service and give that ID to Alfresco Customer Care who can grant the appropriate access.

A similar process will likely be needed for onboarding and offboarding employees.

Quay’s REST APIs (including email invites) should allow us to automate those processes, potentially through APS process definitions.

### Limited Control Over Solution

As a hosted service we will of course have less control over the solution than a deployment we would manage.  Quay also has a solid Enterprise ('on-prem') offering as well if in the future we decide we require more control over the solution (which should support mirroring with quay.io in the future).
