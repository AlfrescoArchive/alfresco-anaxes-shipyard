# 10. Managing secrets used by DBP within the Kubernetes deployment

Date: 2018-05-29

## Status

Accepted

## Context

Many of the components the Digital Business Platform have files or credentials that should be protected within the Kubernetes deployment.

Kubernetes has the concept of [secrets](https://kubernetes.io/docs/concepts/configuration/secret/) within the deployment however certain measures need to be applied to ensure the security of these objects.

There is no turn key solution for securing data within Kubernetes however there are several levels of security that can be applied.

### Identification of Secrets
The first and most important issue to first solve is identifying what needs to be kept as a Secret from Kubernetes Users. This includes license files, credentials for databases, credentials for connecting to external service providers, encryption keys and other business related information that may be considered secrets. As an example, s3 connector credential setup for ACS would need to be stored as a Secret object in Kubernetes.

### Kubernetes Best Practices
A second step in securing these elements would be to follow [Kubernetes best practices](https://kubernetes.io/docs/concepts/configuration/secret/#best-practices) on securing secrets. Following these concepts we identified several risks and practices that need to be addressed.

[Role Based Access Control](https://kubernetes.io/docs/reference/access-authn-authz/rbac/#referring-to-resources) policies should be applied at the Application and Direct Kubernetes User levels.

Enabling [Auditing](https://kubernetes.io/docs/tasks/debug-application-cluster/audit/) at cluster level is another step to take in order to have a cronological set of records documenting user activities.

Applications should not expose any secret data through logging or transmitting them to an untrusted party.

[Encryption at Rest](https://kubernetes.io/docs/tasks/administer-cluster/encrypt-data/) (within Kubernetes etcd storage) should be enabled however this implementations is in alpha state in Kubernetes 1.10.

Secret Kubernetes Manifests should not be shared or checked in to a source repository with Base64 encoding. This security risk has multiple fixes in the Kubernetes community, out of which we believe [sealed-secrets](https://github.com/bitnami-labs/sealed-secrets) and [helm-secrets](https://github.com/futuresimple/helm-secrets) are great candidates for resolving this.

### Integration with External Providers
The third posible layer of security would be a Kubernetes integration with an external secret provider where only the pods using that secret will be able to see the secret data, the secret itself would not be saved as a Kubernetes object. For this layer the current solution on the market which we investigated would be the [vault-operator](https://github.com/coreos/vault-operator) as a Kubernetes implementation that connects to [Hashicorp Vault](https://www.vaultproject.io/) to fetch secrets.

## Decision

We will be transforming all configuration and files that we believe that could hold secrets into Kubernetes secret objects. Aside from that, the helm charts that we produce will provision Role Based Access Control policies to control the components access to Kubernetes objects.

## Consequences

As a result of this decision the storage and associated level of security (for direct Kubernetes Users) of those secrets is the responsibility of the customer.
