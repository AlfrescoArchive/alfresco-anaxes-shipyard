#!/bin/bash

URL=$(kubectl get service -l app=nginx-ingress -l component=controller -o=jsonpath={.items[0].status.loadBalancer.ingress[0].hostname})
echo "https://$URL"