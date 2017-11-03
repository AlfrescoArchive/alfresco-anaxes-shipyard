/*
 * Copyright 2017 Alfresco Software, Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.deployment.appTest;

import java.util.List;
import java.util.Properties;

import org.testng.annotations.BeforeSuite;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class AppAbstract
{
    protected static String restApiUrl;
    protected static String appUrl;
    private static String clusterType;
    private static String clusterNamespace;
    Properties appProperty = new Properties();
    KubernetesClient client = new DefaultKubernetesClient();

    /**
     * The before suit will load test properties file and load the same.
     */
    @BeforeSuite
    public void initialSetup() throws Exception
    {
        appProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
        clusterType = readProperty("cluster.type");
        clusterNamespace = readProperty("cluster.namespace").toLowerCase();
        if (clusterNamespace == null)
        {
            throw new IllegalStateException("Cluster namespace is required , please set namespace details in the properties file");
        }
        if ((clusterType == null) || ("minikube".equalsIgnoreCase(clusterType)))
        {
            restApiUrl = getUrlForMinikube(clusterNamespace, "backend");
            appUrl = getUrlForMinikube(clusterNamespace, "ui");
        }
        else
        {
            restApiUrl = getUrlForAWS(clusterNamespace, "backend");
            appUrl = getUrlForAWS(clusterNamespace, "ui");
        }

        if (restApiUrl.isEmpty() || appUrl.isEmpty())
        {
            throw new Exception("Cluster is not set up correctly");
        }
        restApiUrl = restApiUrl + "/hello";
    }

    private String readProperty(String propertyType)
    {
        return appProperty.getProperty(propertyType);
    }

    /**
     * To find the get service url of minikube
     */
    private String getUrlForMinikube(String nameSpace, String runType)
    {
        String url = client.getMasterUrl().toString();
        List<Service> service = client.services().inNamespace(nameSpace).list().getItems();
        for (Service each : service)
        {
            if (each.getMetadata().getName().contains(runType))
            {
                url = url.replace("https", "http");
                url = url.replace(":8443", ":" + each.getSpec().getPorts().get(0).getNodePort());
            }
        }
        return url ;
    }

    /**
     * To find the load balancer required for testing
     */
    private String getUrlForAWS(String nameSpace, String runType)
    {
        String url = null;
        List<Service> service = client.services().inNamespace(nameSpace).list().getItems();
        for (Service each : service)
        {

            if (each.getMetadata().getName().contains(runType))
            {
                url = each.getStatus().getLoadBalancer().getIngress().get(0).getHostname();
            }
        }
        return "http://" + url;
    }

}
