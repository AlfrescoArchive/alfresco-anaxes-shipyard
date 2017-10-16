# Welcome

This folder contains a sample Spring boot based service that exposes an endpoint of <code>/hello</code>.

The service connects to a Postgres database to retrieve the welcome message to return.

# Build & Test

To build and test the service execute the following command:

    mvn clean package

NOTE: This will also build and push a Docker image named "anaxes-hello-service" to your local registry.

# Run

To run the service execute the following command:

    java -jar target/anaxes-hello-service-[version].jar