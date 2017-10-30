# How to run a Kubernetes cluster

## Locally via Minikube

### Download Tools for Local Minikube Deployment

1. Install Prerequisites for Minikube:

    a. Hypervisor (e.g. [VirtualBox](https://www.virtualbox.org/wiki/Downloads))

    b. [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

    c. [Helm Client](https://docs.helm.sh/using_helm/#installing-helm)

1. Install [Minikube](https://github.com/kubernetes/minikube/releases).

### Start Minikube

1. Start Kubernetes Cluster:

```bash
minikube start
```

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

*Useful resource*: https://kubernetes.io/docs/getting-started-guides/minikube/

## In AWS via Kops

### Download Tools for AWS Deployment

1. Install prerequisites for `kops`

    a. [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)

    b. [Helm Client](https://docs.helm.sh/using_helm/#installing-helm)

    c. [AWS CLI](https://aws.amazon.com/cli/) (Note: install `awscli`, and not `aws-shell`).

    d. A compatible of [python](https://www.python.org)

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

    If you followed the advice to create a versioned bucket, you will need to delete all the versioned objects before deleting the bucket. This is easiest achieved via the [AWS SDK for Python](https://boto3.readthedocs.io) (which will already have been installed as part of the AWS CLI).

    _This script comes from a comment on a [gist](https://gist.github.com/weavenet/f40b09847ac17dd99d16) by
    [Philip Dowie](https://github.com/jnawk)._

    ```python
    import boto3
    import sys

    bucket_name = sys.argv[1]
    session = boto3.Session()
    s3 = session.resource(service_name='s3')
    bucket = s3.Bucket(bucket_name)
    bucket.object_versions.delete()
    bucket.delete()
    ```

*Notes*:

1. This is a single-AZ deployment, and thus is not HA.
1. This cluster is using kubenet. [Other CNI implementations can be installed](https://github.com/kubernetes/kops/blob/master/docs/networking.md).