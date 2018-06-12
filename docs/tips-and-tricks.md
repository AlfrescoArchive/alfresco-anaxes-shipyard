# Tips and Tricks

This page provides various tips and tricks we have found whilst working with Docker and Kubernetes that you may find useful.

# Viewing Pod Details & Logs

Sometimes it can be really useful to see more details of a running pod (especially the events when troubleshooting) to do this run the following command:

```bash
kubectl describe pod [pod-name]
```

Viewing the logs for a pod can also be really useful, to do this run the following command:

```bash
kubectl logs -f [pod-name]
```

# Using Locally Built Docker Image In Minikube

Minikube has it's own docker process running, the details of which are exposed via ```minikube docker-env```.

This makes it possible to build and test a Docker image locally without having to push the image to a remote repository. Use the following command to setup your shell to use minikube's internal docker registry:

```bash
eval $(minikube docker-env)
```

Now build your docker image as normal and it will be pushed to minikube's local registry.

As long as the helm chart you're deploying is configured to use a ```pullPolicy``` of ```IfNotPresent``` (if not, override the appropriate value using --set if the chart allows), minikube will use the local image.

# Kubectx

It's likely that you'll have multiple contexts you switch between using ```kubectl config use-context [context-name]```. If you like to optimise your command line usage there is a small utility called [kubectx](https://github.com/ahmetb/kubectx) that lists and switches contexts for you with slightly less characters to type!

To list the contexts available type:

```bash
kubectx
```

To switch to a different context type:

```bash
kubectx [context-name]
```

# SSH into AWS cluster

If you followed the [steps](./running-a-cluster.md#in-aws-via-kops) to create an AWS cluster using kops you should have an SSH key available.

Run the following commands using that key to access the remote master or node machines.

```bash
ssh-add <your-path>/anaxes_bastion
ssh-add -L
ssh -A admin@<bastion_elb_a_record>
ssh admin@<master/node_ip>
```

# Cluster Troubleshooting

### Node Status is NotReady

Sometimes, worker nodes will enter a "NotReady" state, which will prevent Kubernetes from scheduling pods on the node.

Let's run **kubectl get nodes** to get info about the nodes

```
user@alfresco-anaxes-shipyard ~ $ kubectl get nodes
NAME                            STATUS    AGE       VERSION
ip-172-20-37-236.ec2.internal   NotReady  1d        v1.7.4
ip-172-20-52-154.ec2.internal   Ready     1d        v1.7.4
ip-172-20-62-76.ec2.internal    NotReady  1d        v1.7.4
ip-172-20-66-79.ec2.internal    NotReady  1d        v1.7.4
```

If it is an AWS cluster and using kops it is possible to get similar information with:

***kops validate cluster --name $CLUSTER_NAME --state=s3://$BUCKET_NAME***

```
user@alfresco-anaxes-shipyard ~ $ kops validate cluster --name $CLUSTER_NAME --state=s3://$BUCKET_NAME

Validating cluster helloworld.dev.alfresco.me

INSTANCE GROUPS
NAME               ROLE       MACHINETYPE   MIN MAX   SUBNETS
bastions           Bastion    t2.micro        1	1     utility-us-east-1a,utility-us-east-1b
master-us-east-1a  Master     t2.medium       1	1     us-east-1a
nodes              Node       t2.medium       3	3     us-east-1a,us-east-1b

NODE STATUS
NAME                            ROLE    READY
ip-172-20-37-236.ec2.internal   node    False
ip-172-20-52-154.ec2.internal   master  True
ip-172-20-62-76.ec2.internal    node    False
ip-172-20-66-79.ec2.internal    node    False

Validation Failed
Ready Master(s) 1 out of 1.
Ready Node(s) 0 out of 3.

No nodes found in validationCluster
```

To discover the reason behind the error, we can use **kubectl describe**:
```
kubectl describe node ip-172-20-37-236.ec2.internal

...
Conditions:
  Type          Status          LastHeartbeatTime                       LastTransitionTime                      Reason                  Message
  ----          ------          -----------------                       ------------------                      ------                  -------
  OutOfDisk     Unknown         Thu, 30 Nov 2017 13:50:56 +0000         Thu, 30 Nov 2017 13:40:522 +0000 +0000         NodeStatusUnknown       Kubelet stopped posting node status.
  Ready         Unknown         Thu, 30 Nov 2017 13:50:56 +0000         Thu, 30 Nov 2017 13:40:22 +0000 +0000         NodeStatusUnknown       Kubelet stopped posting node status.

...
```
We can see from the output above that the node is running out of disk space.

To free some spaces we have the following options:

#### Option 1: Delete all unused namespaces and releases

Below a sample script:
```
namespaces_to_delete=($(kubectl get namespaces | awk '{print $1}' | awk 'NR>1'))
namespaces_to_keep=(default kube-public kube-system)
revisions_to_delete=($(helm ls | awk '{print $1}' | awk 'NR>1'))
revisions_to_keep=()

array_contains () {
    local seeking=$1; shift
    local in=1
    for element; do
        if [[ "$element" == "$seeking" ]]; then
            in=0
            break
        fi
    done
    return $in
}

# deleting specific ns

# for el in "${namespaces_to_delete[@]}"; do
#   if ! array_contains $el "${namespaces_to_keep[@]}" ; then
#     kubectl delete ns $el
#   fi
# done

# deleting all ns (default kube-public and kube-system will not be deleted anyway)
# kubectl delete ns -all

for el in "${revisions_to_delete[@]}"; do
  if ! array_contains $el "${revisions_to_keep[@]}" ; then
    helm delete $el
  fi
done
```

#### Option 2: Delete node docker images

Get the Load Balancer DNSName to get into the bastion (if any), ssh into the bastion and then into the node/master:

```
clusterName=helloworld2
dnsname=$(aws elb describe-load-balancers --query "LoadBalancerDescriptions[?starts_with(DNSName,'bastion-$clusterName')].DNSName" --output text)

ssh -A admin@$dnsname
ssh admin@<master/node_ip>
```

and use [docker rmi command](https://docs.docker.com/engine/reference/commandline/image_rm/) to delete images


#### Option 3: Rolling Update

If the nodes are too slow and previous option cannot be run, it would possible to force a [rolling update of the cluster](https://kubernetes.io/docs/reference/generated/kubectl/kubectl-commands#rolling-update) to free some memory from the nodes.

Example of rolling update with Kops
```
kops rolling-update cluster $CLUSTER_NAME --state=s3://$BUCKET_NAME --cloudonly --force --yes
```

Perform Option 1 or/and 2 to remove unnecessary space
