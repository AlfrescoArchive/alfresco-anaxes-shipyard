# How to Run a Kubernetes Cluster

## Locally via Minikube

### Download Tools for Local Minikube Deployment

1. Install Prerequisites for Minikube:

    a. [Hypervisor](https://kubernetes.io/docs/tasks/tools/install-minikube/#install-a-hypervisor)

    b. [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

    c. [Helm Client](https://docs.helm.sh/using_helm/#installing-helm)

    d. [Docker](https://docs.docker.com/engine/installation/#desktop)

1. Install [Minikube](https://github.com/kubernetes/minikube/releases).

### Start Minikube

1. Start Kubernetes Cluster:

```bash
minikube start
```

*Note*: When starting Minikube it is recommended to give it plenty of memory for hosting containers. You can do this by adding parameter --memory=6144 to minikube start command.

2. Install [(Helm) Tiller](https://docs.helm.sh/using_helm/#installing-tiller).

3. If you want to access the dashboard:

```bash
 minikube dashboard
```

### Stop and Delete Minikube Resources

1. Stop Kubernetes Cluster:

```bash
minikube stop
```

2. Delete Kubernetes Cluster:

```bash
minikube delete
```

*Notes*:

1. Features that require a Cloud Provider will not work in Minikube. (e.g. LoadBalancers)
1. Minikube runs a single-node Kubernetes cluster inside a VM on your laptop.

*Useful resource*: [Running Kubernetes Locally via Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/).

## In AWS via Kops

### Download Tools for AWS Deployment

1. Install prerequisites for `kops`

    a. [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

    b. [Helm Client](https://docs.helm.sh/using_helm/#installing-helm)

    c. [AWS CLI](https://aws.amazon.com/cli/) (Note: install `awscli`, and not `aws-shell`).

    d. A compatible version of [python](https://www.python.org)

1. Install [kops](https://github.com/kubernetes/kops#installing)

### Set Up and Start Kops Cluster

1. Set Up Required Resources

    * Create [AWS resources](https://github.com/kubernetes/kops/blob/master/docs/aws.md#setup-your-environment) needed for your cluster.

    Note: using a gossip-based cluster is simpler than one where you need to create DNS resources.

1. [Create the cluster](https://github.com/kubernetes/kops/blob/master/docs/aws.md#create-cluster-configuration)

    This will take a few minutes to create the EC2 instances, set up Kubernetes and make the ELB available.

    Note: currently you need a default RSA ssh key prior to creating the cluster.

1. Install [(Helm) Tiller](https://docs.helm.sh/using_helm/#installing-tiller).

### Stop and Delete AWS Resources

1. [Delete the cluster](https://github.com/kubernetes/kops/blob/master/docs/aws.md#delete-the-cluster)

    This deletes the EC2 instances and ELB.

1. Delete the S3 Bucket

    If you followed the advice to create a versioned bucket, you will need to [delete all the versioned objects](https://docs.aws.amazon.com/AmazonS3/latest/dev/delete-or-empty-bucket.html) before deleting the bucket.
