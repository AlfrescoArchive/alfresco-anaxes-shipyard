# 5. Base Java Docker Image Composition

Date: 2017-10-23

## Status

Accepted

## Context

As part of our efforts around containerized deployment of the Alfresco Digital Business Platform we should standardize on a base Docker Image for Java-based services that Alfresco engineering teams, other internal groups in the organization, and customers and partners can use.

While there are a few popular choices and de facto standards such as OpenJDK, we need to consider the support and legal implications of that choice, particularly since we could be considered as ‘distributing’ all layers in that image, including the OS and Java binaries.

We could potentially workaround being the distributors of sensitive layers using techniques like init containers, composite containers, or foreign layers.

## Decision

We will start from the official CentOS 7 Docker image and add the Oracle server JRE binaries for the first iteration of the Alfresco base Java image.

This allows us to start from a popular OS and Java runtime which are already in the supported ACS and APS stacks and QuickStart AMIs, and closer to the most popular customer OS, RHEL, than others.  We also have precedence in distributing the Oracle JRE in our current installer.

The CentOS image also contains very few known vulnerabilities which we can easily patch at the moment.

While Alpine is an attractive choice due to its simplicity, it may be more difficult to debug and support, and the majority of our components are not yet architected to take advantage of the smaller size and lower resource consumption.

While distribution workarounds mentioned above were considered, it was felt that they are not common practice and are likely to produce more problems than they solve.

The summary [![report of the comparison](https://img.shields.io/badge/report%20of%20the%20comparison-PRIVATE-red.svg)](https://ts.alfresco.com/share/s/bqDcnHWpSrSGybJhMxf93A) contains more details.

The base image Dockerfile is [here](https://github.com/Alfresco/alfresco-docker-base-java/blob/master/Dockerfile).

## Consequences

### Support
It’s unclear what the expectations are around our support of the OS and Java included in this image, especially around security issues.  The Platform Services team should review this further with support.

### Legal
As with most Linux-based OSes, there are GPL components which, again, we could be considered as distributing, so we should have legal determine any risk there or with using Oracle JRE even though there is a precedence with our installer.
