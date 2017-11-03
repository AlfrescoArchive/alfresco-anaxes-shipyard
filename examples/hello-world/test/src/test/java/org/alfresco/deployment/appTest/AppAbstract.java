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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeSuite;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.utils.NullArgumentException;

public class AppAbstract
{
    protected static String restApiUrl;
    protected static String appUrl;
    private static String clusterType;
    private static String clusterNamespace;
    Properties appProperty = new Properties();
    KubernetesClient client = new DefaultKubernetesClient();
    final int RETRY_COUNT = 5;
    private static Log logger = LogFactory.getLog(AppAbstract.class);

    /**
     * The before suit will load test properties file and load the same.
     */
    @BeforeSuite
    public void initialSetup() throws Exception
    {
        appProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
        clusterType = readProperty("cluster.type");
        clusterNamespace = readProperty("cluster.namespace");
        if (clusterNamespace == null)
        {
            throw new IllegalStateException("Cluster namespace is required , please set namespace details in the properties file");
        }
        clusterNamespace = clusterNamespace.toLowerCase();
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
     * 
     * @throws InterruptedException
     */
    private String getUrlForMinikube(String nameSpace, String runType) throws Exception
    {
        String url = client.getMasterUrl().toString();
        List<Service> service = retryUntilServiceAvailable(nameSpace);
        for (Service each : service)
        {
            if (each.getMetadata().getName().contains(runType))
            {
                url = url.replace("https", "http");
                url = url.replace(":8443", ":" + each.getSpec().getPorts().get(0).getNodePort());
            }
        }
        return url;
    }

    /**
     * To find the load balancer required for testing
     * 
     * @throws InterruptedException
     */
    private String getUrlForAWS(String nameSpace, String runType) throws InterruptedException
    {
        String url = null;
        List<Service> service = retryUntilServiceAvailable(nameSpace);
        for (Service each : service)
        {
            logger.info(each.getMetadata().getName());
            if (each.getMetadata().getName().contains(runType))
            {
                int i = 0;
                while (i <= RETRY_COUNT)
                {
                    if (each.getStatus().getLoadBalancer().getIngress() == null)
                    {

                        logger.info("retrying to get the url value correctly");
                        Thread.sleep(10000);
                        i++;
                    }
                    else
                    {
                        url = each.getStatus().getLoadBalancer().getIngress().get(0).getHostname();
                        break;
                    }
                }
            }
        }
        return "http://" + url;
    }

    /**
     * re try until the service is available
     * 
     * @throws InterruptedException
     */
    private List<Service> retryUntilServiceAvailable(String nameSpace) throws InterruptedException
    {
        List<Service> service;
        int i = 0;
        while (i <= RETRY_COUNT)
        {
            service = client.services().inNamespace(nameSpace).list().getItems();
            if ((service.size() == 0))
            {
                logger.info(String.format("the service is empty for round [%s] so planning to wait 10 seconds", i));
                Thread.sleep(10000);
                i++;
            }
            else
            {
                logger.info(String.format("the service is back after [%s] retries", i));
                return service;
            }
        }
        throw new NullArgumentException("The service was never up and running after " + i + "tries");
    }
}
