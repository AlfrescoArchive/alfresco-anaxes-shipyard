# 2. use-heptio-cloudformation-template

Date: 2017-09-26

## Status

**Propsosed** by Gavin Cornwell (26th September 2017)

## Context

A majority of our customers will have not come across or used Kubernetes before we therefore need to provide these customers a way to provision a cluster quickly.

A tool called minikube can be used on laptops for development and evaluation purposes. For a production ready stack we need to provide a way for customers to provision a cluster in AWS using a mechanism a non-IT savvy person can follow.

[HeptIO](https://heptio.com/) provides a [CloudFormation template](https://aws.amazon.com/quickstart/architecture/heptio-kubernetes/) for this purpose.

## Decision

We will **NOT** use the HeptIO CloudFormation template because HeptIO themselves clearly state that the stack generated should only be used for proof of concept (PoC), experimentation, development, and small internal-facing projects.

Furthermore the HeptIO template does not provide a mechanism to upgrade the Kubernetes version used by the cluster, the whole stack needs to be torn down and replaced.

## Consequences

Using an alternative solution we get a production ready Kubernetes cluster that can be managed.

It will require more work to get an easy-to-deploy user-friendly environment setup on AWS.

## Story

[DEPLOY-35](https://issues.alfresco.com/jira/browse/DEPLOY-35)