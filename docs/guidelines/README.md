# Guidelines

The Anaxes project provides various guidelines from creating Docker images through to recommended project structures.

## Repositories

## Building

## Testing

## Docker

## Helm Chart

## ADF Applications

For deploying ADF Applications into production it is highly recommended to perform a distribution build and generate an archive, ideally a tar file, for packaging within a Docker container based from an HTTP server image, such as [nginx](https://hub.docker.com/_/nginx/). An example of this approach can be found [here](https://github.com/Alfresco/alfresco-anaxes-hello-world-ui-deployment).

As well as size and performance benefits this approach avoid the [node](https://hub.docker.com/r/_/node/) Docker image which at the time of writing has a number of un-fixable security vulnerabilities.

The use of the Angular proxy feature is also discouraged as this prevents the back-end from being scaled independently from the front-end.

TODO: Add details of ideal context path and "base href" for deployment?