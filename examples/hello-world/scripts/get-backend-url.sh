#!/bin/bash

if [ $# -lt 1 ] ; then 
  echo "usage: get-backend-url.sh <release-name> [namespace]"
  exit 1
fi

RELEASE=$1-nginx-ingress-controller

if [ $# == 2 ] ; then 
  NAMESPACE="--namespace $2"
else
  NAMESPACE=""
fi

CONTEXT=$(kubectl config current-context)

if [[ $CONTEXT == "minikube" ]] ; then
    IP=$(minikube ip)
    PORT=$(kubectl get service $RELEASE $NAMESPACE -o jsonpath={.spec.ports[0].nodePort})
    echo "http://$IP:$PORT/hello-service/hello"
else
    DNSNAME=$(kubectl get services $RELEASE $NAMESPACE -o jsonpath={.status.loadBalancer.ingress[0].hostname})
    echo "http://$DNSNAME/hello-service/hello"
fi