# How to Run a Kubernetes Cluster

This page provides details on creating and running a Kubernetes cluster both locally for development, using Minikube and remotely on AWS, using kops.

# Locally via Minikube

## Download Tools for Local Minikube Deployment

1. Install Prerequisites for Minikube:

    a. [Hypervisor](https://kubernetes.io/docs/tasks/tools/install-minikube/#install-a-hypervisor)

    b. [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

    c. [Helm Client](https://docs.helm.sh/using_helm/#installing-helm)

    d. [Docker](https://docs.docker.com/engine/installation/#desktop)

1. Install [Minikube](https://github.com/kubernetes/minikube/releases).

## Start Minikube

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

## Stop and Delete Minikube Resources

1. Stop Kubernetes Cluster:

```bash
minikube stop
```

2. Delete Kubernetes Cluster:

```bash
minikube delete
```

*Notes*:

* Features that require a Cloud Provider will not work in Minikube. (e.g. LoadBalancers)
* Minikube runs a single-node Kubernetes cluster inside a VM on your laptop.

*Useful resource*: [Running Kubernetes Locally via Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/).

# In AWS via Kops

## Download Tools for AWS Deployment

1. Install prerequisites for `kops`.

    a. [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

    b. [Helm Client](https://docs.helm.sh/using_helm/#installing-helm)

    c. [AWS CLI](https://aws.amazon.com/cli/) (Note: install `awscli`, and not `aws-shell`).

   
2. Install [kops](https://github.com/kubernetes/kops#installing).

## Set Up and Start Kops Cluster

1. Create an SSH key.

    ```bash
    ssh-keygen -t rsa -b 4096 -C "anaxes_bastion" 
    ```

2. [Set Up Required Resources](https://github.com/kubernetes/kops/blob/master/docs/aws.md#setup-your-environment) needed for your cluster.

    Note: Using a gossip-based cluster is much simpler than creating a DNS based cluster.

3. Create the cluster using the SSH key created in step 1 and AWS s3 bucket created 
   in step 2.

   Note this will take a few minutes to create the EC2 instances, set up Kubernetes and make the ELB available.

   ```bash
   export KOPS_NAME="<my kops name>"
   export KOPS_STATE_STORE="s3://<my s3 bucket name>"
   
   kops create cluster \
     --ssh-public-key ps-cluster.pub \
     --name $KOPS_NAME \
     --state $KOPS_STATE_STORE \
     --node-count 2 \
     --zones eu-west-1a,eu-west-1b \
     --master-zones eu-west-1a,eu-west-1b,eu-west-1c \
     --cloud aws \
     --node-size m4.xlarge \
     --master-size t2.medium \
     -v 10 \
     --kubernetes-version "1.8.4" \
     --bastion \
     --topology private \
     --networking weave \
     --yes
   ```
4. Install [(Helm) Tiller](https://docs.helm.sh/using_helm/#installing-tiller).

5. Install the dashboard.

    If you're setting up a production environment use the recommended approach which is more secure.

    ```bash
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml
    ```

    To access the dashboard view [these instructions](https://github.com/kubernetes/dashboard/wiki/Accessing-Dashboard---1.7.X-and-above).

    If you're setting up a development environment use the alternative approach which makes access easier.

    ```bash
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/alternative/kubernetes-dashboard.yaml
    ```

    To access the dashboard view [these instructions](https://github.com/kubernetes/dashboard/wiki/Accessing-Dashboard---1.6.X-and-below).

Provided all the steps were a success, the deployed cluster topology should be similar to that of 
[Kops demo](https://github.com/kris-nova/kops-demo/tree/master/ha-master-private-subdomain):
![](https://github.com/kris-nova/kops-demo/raw/master/ha-master-private-subdomain/k8s-aws-ha-private-master-sub.png)
## Stop and Delete AWS Resources

1. [Delete the cluster](https://github.com/kubernetes/kops/blob/master/docs/aws.md#delete-the-cluster).

    This deletes the EC2 instances and ELB.

2. Delete the S3 Bucket.

    If you followed the advice to create a versioned bucket, you will need to [delete all the versioned objects](https://docs.aws.amazon.com/AmazonS3/latest/dev/delete-or-empty-bucket.html) before deleting the bucket.
