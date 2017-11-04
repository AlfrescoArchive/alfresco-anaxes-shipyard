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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
    final int RETRY_COUNT = 10;
    final long TIMER = 20000;
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
        if ((clusterType.isEmpty()) || ("minikube".equalsIgnoreCase(clusterType)))
        {
            restApiUrl = getUrlForMinikube(clusterNamespace, "backend");
            appUrl = getUrlForMinikube(clusterNamespace, "ui");
        }
        else
        {
            restApiUrl = getUrlForAWS(clusterNamespace, "backend");
            appUrl = getUrlForAWS(clusterNamespace, "ui");
        }

        restApiUrl = restApiUrl + "/hello";
        testServiceUp();
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
        url = url.replace("https", "http");
        int i = 0;
        long sleepCount = 0;
        while ((i <= RETRY_COUNT) & (url.contains(":8443")))
        {
            List<Service> service = client.services().inNamespace(nameSpace).list().getItems();
            for (Service each : service)
            {
                if (each.getMetadata().getName().contains(runType))
                {
                    if (each.getSpec().getPorts().size() != 0)
                    {
                        url = url.replace(":8443", ":" + each.getSpec().getPorts().get(0).getNodePort());
                        logger.info("URL details " + url + " in total seconds " + sleepCount / 1000);
                    }
                    break;
                }
            }
            Thread.sleep(TIMER);
            i++;
            sleepCount = sleepCount + TIMER;
            logger.info("Retried to get the URL  - number of retries " + i + "total time taken " + sleepCount / 1000);

        }
        if (url.contains(":8443"))
        {
            throw new Exception("the minikube service url is not available to continue testing - Total seconds - " + sleepCount / 1000);
        }
        return url;
    }

    /**
     * To find the load balancer required for testing
     * 
     * @throws Exception
     */
    private String getUrlForAWS(String nameSpace, String runType) throws Exception
    {
        String url = null;
        int i = 0;
        long sleepCount = 0;
        while ((i <= RETRY_COUNT) & (url == null))
        {
            List<Service> service = client.services().inNamespace(nameSpace).list().getItems();
            for (Service each : service)
            {
                if (each.getMetadata().getName().contains(runType))
                {
                    if (each.getStatus().getLoadBalancer().getIngress().size() != 0)
                    {
                        url = each.getStatus().getLoadBalancer().getIngress().get(0).getHostname();
                        logger.info("URL details " + url + " in total seconds " + sleepCount / 1000);
                    }
                    break;
                }
            }
            Thread.sleep(TIMER);
            i++;
            sleepCount = sleepCount + TIMER;
            logger.info("retrying to get the url - number of retries " + i + "-total time taken " + sleepCount / 1000);

        }
        if (url == null)
        {
            throw new Exception("the aws service url is not available to continue testing -  Total seconds - " + sleepCount / 1000);
        }
        return "http://" + url;
    }

    /**
     * Validate the service is up and running
     * 
     * @throws Exception
     */
    private void testServiceUp() throws Exception
    {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        int i = 0;
        while (i <= RETRY_COUNT)
        {
            logger.info("Validate whether the DNS is all up and running");
            HttpGet getRequest = new HttpGet(restApiUrl);
            response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() == 405)
            {
                logger.info("DNS is up and running after so many retries  " + i);
                httpClient.close();
                break;
            }
            else
            {
                logger.info(String.format("re trying as dns is not ready - retry count " + i));
                response.close();
                Thread.sleep(TIMER);
                i++;
            }
        }
        if (i > RETRY_COUNT)
        {
            throw new Exception("DNS not ready");
        }

    }
}
