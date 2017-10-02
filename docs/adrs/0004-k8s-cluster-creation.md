# 4. k8s-cluster-creation

Date: 2017-10-02

## Status

**Propsosed** by Gavin Cornwell

## Context

A majority of our customers will have not come across or used Kubernetes before we therefore need to provide these customers a way to provision a cluster quickly.

A tool called minikube can be used on laptops for development and evaluation purposes. For a production ready stack we need to provide a way for customers to provision a cluster in AWS using a mechanism a less technically literate person can follow.

A list of candidate solutions can be found [here](https://github.com/kubernetes/community/blob/master/sig-aws/kubernetes-on-aws.md).

## Decision

The decision is to go with ...

Details of how this decision was made can be found in [DEPLOY-35](https://issues.alfresco.com/jira/browse/DEPLOY-35).

## Consequences

We can provide customers with a recommended way to deploy a Kubernetes cluster and be confident our deployments will work seamlessly.

There is no easy way to produce an AWS QuickStart with the current tools. It may be technically feasible using kops but it will take a fair amount of effort.