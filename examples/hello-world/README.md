# Hello World Example 

To get familiar with how an Alfresco Engineer or a Solution Developer can build and use a deployment package for Kubernetes we have created a simple hello world app that you can use for reference as you get started.

The application consists of several components:
- database to store the data, postgres in our case
- backend rest service to Create/Read/Update/Delete entries in the db
- frontend app to proxy the backend service

The interactions between the components is shown in the following diagram:

![Sequence-diagram](./diagrams/sequence-diagram.png "sequence-diagram")

## Prerequisites

- A running Kubernetes cluster (this can be [minikube](https://kubernetes.io/docs/getting-started-guides/minikube/) or a cluster on [AWS](https://aws.amazon.com/blogs/compute/kubernetes-clusters-aws-kops/))
- [Helm](https://github.com/kubernetes/helm/blob/master/docs/install.md) client is installed locally and deployed to your cluster
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) is insallted and configured for your cluster

## How to Deploy

1. Create your working namespace:

```bash
kubectl create namespace example 
```

2. Generate a base64 value for your dockercfg, this will allow Kubernetes to access docker-internal.alfresco.com

```bash
cat ~/.docker/config.json | base64 
```

NOTE: If you're using Docker for Mac ensure your "Securely store docker logins in macOS keychain" preference is OFF before running this step.

3. Navigate to the helm folder and insert the base64 string generated in the previous step to <code>.dockerconfigjson</code> in <code>secrets.yaml</code>

4. Create your secret in your previously defined namespace

```bash
kubectl create -f secrets.yaml --namespace example
```

5. Deploy the helm chart in your namespace
```bash
helm install hello-world-app --namespace=example
```

NOTE: If you are using an AWS Kubernetes cluster set the service to type LoadBalancer to prompt AWS to create an ELB that will allow access to the app.



## Running on minikube

1. Find the IP address of your minikube cluster by running the following command:

```bash
minikube ip
```
TODO: Finish steps

## Running on AWS

1. To get access to app we need Kubernetes to generate an ELB, to do this change the service type to LoadBalancer:

```bash
helm install hello-world-app --set service.type=LoadBalancer --namespace=example
```

2. Find the DNS hostname of the ELB by running the following command:

```bash
kubectl get services --namespace dev-gav -o jsonpath='{.items[0].status.loadBalancer.ingress[0].hostname}'
```

TODO: Finish steps

## Appendix

For NodePort service type:
```bash
  export NODE_PORT=$(kubectl get --namespace {{ .Release.Namespace }} -o jsonpath="{.spec.ports[0].nodePort}" services {{ template "fullname" . }})
  export NODE_IP=$(kubectl get nodes --namespace {{ .Release.Namespace }} -o jsonpath="{.items[0].status.addresses[0].address}")
  echo http://$NODE_IP:$NODE_PORT/hello/welcome
```

For LoadBalancer service type:
```bash
  export SERVICE_IP=$(kubectl get svc --namespace {{ .Release.Namespace }} {{ template "fullname" . }} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
  echo http://$SERVICE_IP:{{ .Values.service.externalPort }}/hello/welcome
```

For ClusterIp service type:
```bash
  export POD_NAME=$(kubectl get pods --namespace {{ .Release.Namespace }} -l "app={{ template "fullname" . }}" -o jsonpath="{.items[0].metadata.name}")
  echo "Visit http://127.0.0.1:8088/hello/welcome to use your application"
  kubectl port-forward $POD_NAME 8088:{{ .Values.service.internalPort }}
```