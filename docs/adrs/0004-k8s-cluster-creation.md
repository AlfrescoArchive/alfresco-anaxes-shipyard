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

We have chosen kops as it's the tool the most closely fulfils our current and future requirements, it’s produced by the same team that build Kubernetes and it’s been [endorsed](https://aws.amazon.com/blogs/compute/kubernetes-clusters-aws-kops) by AWS.

## Consequences

We can provide customers with a recommended way to deploy a Kubernetes cluster and be confident our deployments will work seamlessly.

This is an extremely fast moving segment of the market, several large vendors have support for or built upon Kubernetes including Microsoft, Google, Pivotal and Rancher so this decision may need re-visiting regularly.

There is no easy way to produce an AWS QuickStart with the current tools. It may be technically feasible using kops but it will take a fair amount of effort.