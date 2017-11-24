# Guidelines

The Anaxes project provides various guidelines from creating Docker images through to recommended project structures.

## Repositories

## Building

## Testing

This section provides some guidelines for testing the deployment of Helm charts into Kubernetes clusters.

### Setup Cluster

The current recommendation is to deploy and run a cluster that can be used for testing rather than attempting to deploy a whole cluster during a build plan. The [Running a Cluster](../running-a-cluster.md) and [Tips and Tricks](../tips-and-tricks.md) pages can be used to do this.

Unfortunately Minikube needs to run on dedicated hardware (it uses virtualization itself). We currently have one dedicated MacMini build agent running Minikube, this pool will need to be expanded as usage increases.

To avoid conflicts and make testing easier it's highly recommended to deploy into a new namespace, this should be done as part of the "Deploy & Test" stage of the [build plan](#plans).

### Deployment

To test the deployment of Helm charts the usage of the [Fabric8 Java client](https://github.com/fabric8io/kubernetes-client) is recommended. It provides an API for interacting with Kubernetes clusters and can be used to verify pods were deployed as expected, for a working example examine [this project](https://github.com/Alfresco/alfresco-anaxes-hello-world-service).

Any Kubernetes specific testing, for example, ensuring an ELB has been provisioned and is accessible, should be done in these tests.

### End-to-End

As well as the usual unit and integration tests, standalone tests designed to take a target URL and execute against a running system should also be provided. This allows the same set of tests to be shared and run against a local HTTP server, a local Kubernetes cluster or a remote, highly available Kubernetes cluster.

## Docker

## Helm Chart
