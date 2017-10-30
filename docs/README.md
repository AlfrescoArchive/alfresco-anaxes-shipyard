# How to run a Kubernetes cluster?

## Locally

1. Install prerequisites for Minikube:

    a. Hypervisor (e.g. [VirtualBox](https://www.virtualbox.org/wiki/Downloads))

    b. [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

2. Install [Minikube](https://github.com/kubernetes/minikube/releases).
3. Start Kubernetes Cluster:
```
minikube start
```
4. Install [Helm](https://github.com/kubernetes/helm/blob/master/docs/install.md#installing-the-helm-client).
5. If you want to access the dashboard: 
```
 minikube dashboard 
```
6. Stop Kubernetes Cluster: 
```
minikube stop
```
7. Delete Kubernetes Cluster:
```
minikube delete
```

*Notes*: 

1. Features that require a Cloud Provider will not work in Minikube. (e.g. LoadBalancers)
2. Minikube runs a single-node Kubernetes cluster inside a VM on your laptop.

*Useful resource*: https://kubernetes.io/docs/getting-started-guides/minikube/
## In AWS
 
