
# Examples

## Prerequisites

Most examples require a running Kubernetes cluster. You can get the cluster up and running using our ![Tutorial](https://github.com/Alfresco/alfresco-anaxes-shipyard/tree/master/docs/running-a-cluster.md) if you do not have one already.

For examples requiring protected Docker images at Quay.io your cluster must contain a secret with credentials to be able to pull those images:

NOTE: If you're using Docker for Mac ensure your "Securely store docker logins in macOS keychain" preference is OFF (as shown in the diagram below) before running this step.

![Docker Preferences](./diagrams/docker-preferences.png)

1. Login with Docker to Quay.io with your credentials:

```bash
docker login quay.io
```

2. Generate a base64 value for your dockercfg, this will allow Kubernetes to access quay.io

```bash
cat ~/.docker/config.json | base64
```

3. Navigate to the 'examples' folder and insert the base64 string generated in the previous step to <code>.dockerconfigjson</code> in <code>secrets.yaml</code>

4. Create your secret in your previously defined namespace.

```bash
kubectl create -f secrets.yaml --namespace example
```

You should see the output below.

<pre>
secret "quay-registry-secret" created
</pre>