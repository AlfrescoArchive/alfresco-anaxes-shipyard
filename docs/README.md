# Learning Resources 

## How to run Kubernetes cluster locally?

1. Install Minikube. https://kubernetes.io/docs/tasks/tools/install-minikube/
2. Start Kubernetes cluster. https://kubernetes.io/docs/getting-started-guides/minikube/#starting-a-cluster
*Note*: Minikube runs a single-node Kubernetes cluster inside a VM on your laptop.
3. If you want to access the dashboard: minikube dashboard https://kubernetes.io/docs/getting-started-guides/minikube/#dashboard.
4. Stop Cluster. https://kubernetes.io/docs/getting-started-guides/minikube/#stopping-a-cluster
5. Delete Cluster. https://kubernetes.io/docs/getting-started-guides/minikube/#deleting-a-cluster
*Note*: 1. Features that require a Cloud Provider will not work in Minikube. (e.g. LoadBalancers)
        2. If you want to configure Kubernetes https://kubernetes.io/docs/getting-started-guides/minikube/#configuring-kubernetes

## How to run Kubernetes cluster on AWS?