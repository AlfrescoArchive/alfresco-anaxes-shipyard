# Anaxes Hello World App

This Helm chart provides an example deployment of the Anaxes Hello World App. 

The application consists of a simple UI that calls a simple REST API to retrieve a welcome message from a database to display to the user.

The chart is intended to serve as an example of how a team should build, package and deploy to Kubernetes clusters using Anaxes artifcats and best practices.

A Deployment is used to create a ReplicaSet for the UI and the REST API. 

A Service is used to create a gateway to the UI and REST API pods running in the replica set.

This chart depends on the [Postgresql](https://github.com/kubernetes/charts/tree/master/stable/postgresql) chart to provide the database.

You can deploy this chart to a minikube cluster with `helm install hello-world-app` or to a cluster running on AWS using `helm install hello-world-app --set ui.service.type=LoadBalancer --set backend.service.type=LoadBalancer`.

Once deployed you can use the `get-ui-url.sh` and the `get-backend-url.sh` scripts to get the publicly accessible URL for the UI and REST API, respectively.