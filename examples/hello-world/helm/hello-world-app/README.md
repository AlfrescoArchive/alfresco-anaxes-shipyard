# Anaxes Hello World App

This Helm chart provides an example deployment of the Anaxes Hello World App. 

The application consists of a simple UI that calls a simple REST API to retrieve a welcome message from a database to display to the user.

The chart is intended to serve as an example of how a team should build, package and deploy to Kubernetes clusters using Anaxes artifcats and best practices.

A Deployment is used to create a ReplicaSet for the UI and the REST API. 

Services are used to create gateways to the UI and REST API pods running in the replica set.

Ingresses are used to rewrite the paths of the two services and offer externally through one common service.

This chart depends on the following charts to provide the database and ingress path rewrites:
- Postgresql - [https://github.com/kubernetes/charts/tree/master/stable/postgresql](https://github.com/kubernetes/charts/tree/master/stable/postgresql)
- Nginx Ingress - [https://github.com/kubernetes/charts/tree/master/stable/nginx-ingress](https://github.com/kubernetes/charts/tree/master/stable/nginx-ingress)

You can deploy this chart to a minikube or AWS cluster with `helm install hello-world-app`

Once deployed you can use the `get-ui-url.sh` and the `get-backend-url.sh` scripts to get the publicly accessible URL for the UI and REST API, respectively.