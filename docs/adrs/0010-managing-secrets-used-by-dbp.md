# 10. Managing secrets used by DBP within the K8S deployment

Date: 2018-05-29

## Status

Draft

## Context

Many of the components the Digital Business Platform have files or credentials that should be protected within the k8s deployment.

Kubernetes has the concept of [secrets](https://kubernetes.io/docs/concepts/configuration/secret/) within the deployment however certain measures need to be applied to ensure the security of these objects.

There is no turn key solution for securing data within kubernetes however there are several levels of security that can be applied.

The first and most important issue to first solve is identifying what needs to be kept as a secret from kubernetes users. We wrote a list of elements we believe need to be changed to secrets within our deployments.
### link to list maybe or list them here

A second step in securing these elements would be to follow [kubernetes best practices](https://kubernetes.io/docs/concepts/configuration/secret/#best-practices) on securing secrets.

This implies that [Role Based Access Control](https://kubernetes.io/docs/reference/access-authn-authz/rbac/#referring-to-resources) policies are be applied at the Application level and User level.

Enabling [Auditing](https://kubernetes.io/docs/tasks/debug-application-cluster/audit/) at cluster level is another step to take in order to have a cronological set of records documenting user activities.

Applications should not expose any secret data (trough logs) and if possible not use volumes load the data within the containers.

Encryption at rest (within k8s etcd) should be enabled however this implementations is in alpha state in kubernetes 1.10.

Secret kubernetes Manifests should not be shared or checked in to a source repository with Base64 encoding. This layer has multiple implementations in the k8s community, out of which we believe [sealed-secrets](https://github.com/bitnami-labs/sealed-secrets) and [helm-secrets](https://github.com/futuresimple/helm-secrets) are worth mentioning.

The third posible layer of security would be a k8s integration with an external secret provider where only the pods using that secret will be able to see the secret data, the secret itself would not be saved as a kubernetes object. For this layer the only solution out there would be the [vault-operator](https://github.com/coreos/vault-operator) as a kubernetes implementation that connects to hashicorp vault to fetch secrets.

## Decision



## Consequences


