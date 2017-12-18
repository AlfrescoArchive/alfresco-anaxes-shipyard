# 7. Applying Extensions to Docker Images

Date: 2017-12-18

## Status

Proposed

## Context

One of the main advantages of Docker images is their immutability. This means once an image has been tested and verified there is a high confidence it will work as intended in other environments as it doesn't change.

Alfresco allows the core product to the enhanced via external modules in the form of [AMPs](https://docs.alfresco.com/5.2/concepts/dev-extensions-packaging-techniques-amps.html) or [simple JARs](https://docs.alfresco.com/5.2/concepts/dev-extensions-packaging-techniques-jar-files.html).

This results in two big problems:
* How do we release containers with every combination of AMP available?
* How do customers apply their own extensions?

We have three options:
1. Apply extensions at build time, thus retaining the immutability advantage
2. Apply extensions at runtime using a mechanism that doesn't change the contents of the container
3. Apply extensions as the container initializes, breaking immutability.

There are a number of disadvantages of applying extensions at runtime:
* Immutability is lost (WAR file is changed)
* Extension could fail to apply and prevent the container from starting
* Slower startup time as AMP or JAR has to be fetched and applied
* Potential security issue

Conversely, applying extensions at build time means we will be forcing customers to build their own images depending on which official and custom extensions they require.

We investigated potential approaches for option 3 using volumes and initContainers but this increases the complexity of the solution and doesn't resolve all the issues outlined above.

## Decision

Given the number of disadvantages to applying extensions at runtime, customers are already used to applying their own extensions and they're having to learn a new deployment mechanism anyway, we are selecting the build time option.

As a company we will release a small number of images with and without common AMPs applied.

## Consequences

Only supporting extensions applied at build time will require customers to build their own images if they have custom extensions or we don't ship an image with the required combination of extensions.

To mitigate the additional complexity for customers we will provide scripts and clear documentation on how to build their own images to meet their requirements.