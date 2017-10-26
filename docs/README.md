# Learning Resources 

## How to run Kubernetes cluster locally?

1. Install Minikube. https://kubernetes.io/docs/tasks/tools/install-minikube/
2. Start Kubernetes Cluster. https://kubernetes.io/docs/getting-started-guides/minikube/#starting-a-cluster

*Note*: Minikube runs a single-node Kubernetes cluster inside a VM on your laptop.

3. Install Helm. https://github.com/kubernetes/helm/blob/master/docs/install.md#installing-the-helm-client
4. If you want to access the dashboard: minikube dashboard https://kubernetes.io/docs/getting-started-guides/minikube/#dashboard.
4. Stop Kubernetes Cluster. https://kubernetes.io/docs/getting-started-guides/minikube/#stopping-a-cluster
5. Delete Kubernetes Cluster. https://kubernetes.io/docs/getting-started-guides/minikube/#deleting-a-cluster

*Notes*: Features that require a Cloud Provider will not work in Minikube. (e.g. LoadBalancers)

## How to run Kubernetes cluster on AWS?
