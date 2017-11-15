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
sh-add <your-path>/anaxes_bastion
ssh-add -L
ssh -A admin@<bastion_elb_a_record>
ssh admin@<master/node_ip>
```