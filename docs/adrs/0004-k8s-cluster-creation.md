# 4. k8s-cluster-creation

Date: 2017-10-04

## Status

Accepted

## Context

We need to recommend a tool to those customers that do not have an existing Kubernetes cluster.

A tool called minikube can be used on laptops for development and evaluation purposes. For a production ready stack we need to provide a way for customers to provision a cluster in AWS using a mechanism a less technically literate person can follow.

A list of candidate solutions can be found [here](https://github.com/kubernetes/community/blob/master/sig-aws/kubernetes-on-aws.md).

## Decision

We will be suggesting customers use kops to create their Kubernetes cluster because ....

The summary [report of the comparison](https://ts.alfresco.com/share/page/site/DBP/document-details?nodeRef=workspace://SpacesStore/80101afd-9618-44ad-bf0c-db76fb5e747b) also contains links to the full investigation.

## Consequences

We can provide customers with a recommended way to deploy a Kubernetes cluster and be confident our deployments will work seamlessly.

There is no easy way to produce an AWS QuickStart with the current tools. It may be technically feasible using kops but it will take a fair amount of effort.