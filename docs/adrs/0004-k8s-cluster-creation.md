# 4. k8s-cluster-creation

Date: 2017-10-04

## Status

Accepted

## Context

We need to recommend a tool to those customers that do not have an existing Kubernetes cluster.

A tool called minikube can be used on laptops for development and evaluation purposes. For a production ready stack we need to provide a way for customers to provision a cluster in AWS using a mechanism a less technically literate person can follow.

A list of candidate solutions can be found [here](https://github.com/kubernetes/community/blob/master/sig-aws/kubernetes-on-aws.md).

## Decision

We will be informing customers that we use kops internally and it's the cluster upon which we perform all our testing.

## Consequences

We can provide customers with a recommended way to deploy a Kubernetes cluster and be confident our deployments will work seamlessly.

There is no easy way to produce an AWS QuickStart with the current tools. It may be technically feasible using kops but it will take a fair amount of effort.