# Hello World example 

To get familiar with how an Alfresco Engineer or a Solution Developer can build and use a deployment package for minikube / AWS we have created a simple hello world app that you can use for reference as you get started.

The application consists in several components:
- database to store the data, postgres in our case
- backend rest service to Create/Read/Update/Delete entries in the db
- frontend app to proxy the backend service

The interrations between the apps look as presented in the following diagram.

![Sequence-diagram](http://www.plantuml.com/plantuml/png/LP71JiCm38RlVWehzua9mQL2qreLGGK2KQtYmYLRlQqYJQB4tQWOxuwxKR5Ty-Mp_Tlpo3fmrng0HSE64dmaOno-ks1gJBq7q2js0LKLAIZK4IlmeZIhnclBgBBWzYFABb5LuBFkHwsuX2L5JdrpP6LwoNBDUgaZ7M6U0LGucM6Mu8lUDHlDz66thYMlDmJPWCeqmOVx1LvjX-cXJ8V7F4lUEWezIZ4ZobfYV5AFQazc9Rg2V9HBuYvOt6dJ4Qgp5IWu70v_zQf2_aNlR2IydaxnzGciOyBGRBOCOKJIfjMSmBTYYQYIEwOldgo3QoVrSlO3P6iSF93rd5ATkfyouN-eQY2sfd1EP9wRMna9veBaRhy0 "sequence-diagram")

## How to Deploy

Prerequisites: 

- A kubernetes cluster up and ready to accept helm deploys AWS or minikube
- Helm client
- Kubectl connected to your cluster

1. Create your working namespace:
```bash
kubectl create namespace example 
```

2. Go within the helm folder and add your base64 value for the dockercfg, this will help you with the registry authentications.
add the base64 string in the .dockerconfigjson: base64
You can get the base64 value by running
```bash
cat ~/.docker/config.json | base64 
```

3. reate your secret in your previously defined namespace

```bash
kubectl create -f secrets.yaml -namespace example
```

4. Deploy the helm chart in your namespace
```bash
helm install hello-world-app --namespace=example
```
! If you are using a AWS kubernetes cluster set the service to type LoadBalancer as it will be mapped to a load balancer.
```bash
helm install hello-world-app --set service.type=LoadBalancer --namespace=example
```

5. When you run the helm command you will also get info on how to connect to your cluster:

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
  export POD_NAME=$(kubectl get pods --namespace {{ .Release.Namespace }} -l "app={{ template "name" . }},release={{ .Release.Name }}" -o jsonpath="{.items[0].metadata.name}")
  echo "Visit http://127.0.0.1:80/hello/welcome to use your application"
  kubectl port-forward $POD_NAME 80:{{ .Values.service.externalPort }}
```
