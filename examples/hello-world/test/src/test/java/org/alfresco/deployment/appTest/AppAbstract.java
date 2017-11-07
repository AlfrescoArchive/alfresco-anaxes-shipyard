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

import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class AppAbstract
{
    private static final String CLUSTER_TYPE = "cluster.type";
    private static final String CLUSTER_NAMESPACE = "cluster.namespace";
    private static Log logger = LogFactory.getLog(AppAbstract.class);
    
    private String clusterType;
    private String clusterNamespace;
    private boolean isMinikubeCluster = false;
    private Properties appProperty = new Properties();
    private KubernetesClient client = new DefaultKubernetesClient();
    private final int RETRY_COUNT = 10;
    private final long SLEEP_DURATION = 15000;

    /**
     * Perform common setup, determines cluster type and namespace
     */
    public void commonSetup() throws Exception
    {
        // load properties file
        appProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
        
        // get cluster type, first check system property, fall back to properties file
        clusterType = System.getProperty(CLUSTER_TYPE);
        if (clusterType == null)
        {
            clusterType = appProperty.getProperty(CLUSTER_TYPE);
        }
        
        // get cluster namespace, first check system property, fall back to properties file
        clusterNamespace = System.getProperty(CLUSTER_NAMESPACE);
        if (clusterNamespace == null)
        {
            clusterNamespace = appProperty.getProperty(CLUSTER_NAMESPACE);
        }
        
        logger.info("clusterType: " + clusterType);
        logger.info("clusterNamespace: " + clusterNamespace);

        if (clusterNamespace == null || clusterNamespace.isEmpty())
        {
            throw new IllegalStateException("Cluster namespace is required, set namespace details in system property or properties file");
        }
        
        // ensure namespace is lower case
        clusterNamespace = clusterNamespace.toLowerCase();
        
        // set cluster type flag
        if (clusterType == null || clusterType.isEmpty() || "minikube".equalsIgnoreCase(clusterType))
        {
            isMinikubeCluster = true;
        }
    }

    /**
     * Determines whether the cluster type is minikube
     * 
     * @return true if the cluster under test is minikube, false otherwise
     */
    protected boolean isMinikubeCluster()
    {
        return isMinikubeCluster;
    }

    /**
     * Finds a service url running in minikube.
     */
    protected String getUrlForMinikube(String runType) throws Exception
    {
        logger.info("Retrieving " + runType + " URL for minikube...");

        String clusterUrl = client.getMasterUrl().toString();
        logger.info("cluster URL: " + clusterUrl);
        
        int nodePort = -1;
        int i = 0;
        long sleepTotal = 0;
        while ((i <= RETRY_COUNT) & (nodePort == -1))
        {
            // find the port number for the given 'runType'
            List<Service> services = client.services().inNamespace(clusterNamespace).list().getItems();
            logger.info("Found " + services.size() + " services");
            for (Service service : services)
            {
                if (service.getMetadata().getName().contains(runType))
                {
                    logger.info("Looking up nodePort for service: " + service.getMetadata().getName());
                    if (service.getSpec().getPorts().size() != 0)
                    {
                        nodePort = service.getSpec().getPorts().get(0).getNodePort();
                        break;
                    }
                }
            }
            
            // try again if url was not found
            if (nodePort == -1)
            {
                logger.info("nodePort is not available, sleeping for " + (SLEEP_DURATION/1000) + " seconds, retry count: " + i);
                Thread.sleep(SLEEP_DURATION);
                i++;
                sleepTotal = sleepTotal + SLEEP_DURATION;
            }
        }
        
        if (nodePort != -1)
        {
            return clusterUrl.replace("https", "http").replace("8443", Integer.toString(nodePort));
        }
        else
        {
            throw new IllegalStateException("Failed to find nodePort for runType '" + runType + 
                        "' in namespace '" + clusterNamespace + "' after " + sleepTotal + " seconds");
        }
    }

    /**
     * Finds a service url running in AWS.
     * 
     * @throws Exception
     */
    protected String getUrlForAWS(String runType) throws Exception
    {
        logger.info("Retrieving " + runType + " URL for AWS...");
        logger.info("cluster URL: " + client.getMasterUrl().toString());
        
        String url = null;
        int i = 0;
        long sleepTotal = 0;
        while ((i <= RETRY_COUNT) & (url == null))
        {
            List<Service> services = client.services().inNamespace(clusterNamespace).list().getItems();
            logger.info("Found " + services.size() + " services");
            for (Service service : services)
            {
                if (service.getMetadata().getName().contains(runType))
                {
                    logger.info("Looking up hostname for service: " + service.getMetadata().getName());
                    if (service.getStatus().getLoadBalancer().getIngress().size() != 0)
                    {
                        url = service.getStatus().getLoadBalancer().getIngress().get(0).getHostname();
                        break;
                    }
                }
            }
            
            // try again if url was not found
            if (url == null)
            {
                logger.info("URL is not available, sleeping for " + (SLEEP_DURATION/1000) + " seconds, retry count: " + i);
                Thread.sleep(SLEEP_DURATION);
                i++;
                sleepTotal = sleepTotal + SLEEP_DURATION;
            }
        }
        
        if (url == null)
        {
            throw new IllegalStateException("Failed to find url for runType '" + runType + 
                        "' in namespace '" + clusterNamespace + "' after " + sleepTotal + " seconds");
        }
        
        return "http://" + url;
    }

    /**
     * Waits for the given URL to become available, unless the timeout period is reached, 
     * in which case an exception is thrown.
     * 
     * @throws IllegalStateException
     */
    protected void waitForURL(String url) throws Exception
    {
        logger.info("Waiting for '" + url + "' to become available...");
        
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        int i = 0;
        while (i <= RETRY_COUNT)
        {
            try
            {
                logger.info("building request");
                HttpGet getRequest = new HttpGet(url);
                RequestConfig config = RequestConfig.custom()
                            .setSocketTimeout(2000)
                            .setConnectionRequestTimeout(2000)
                            .setConnectTimeout(2000).build();
                getRequest.setConfig(config);
                logger.info("executing request");
                response = httpClient.execute(getRequest);
                logger.info("received response");
                
                // grab something from the response to trigger send
                int status = response.getStatusLine().getStatusCode();
                logger.info("response status code: " + status);
                
                // any response here means the URL is accessible 
                logger.info("URL is available, took " + i + " retries");
                httpClient.close();
                break;
            }
            catch (UnknownHostException uhe)
            {
                if (response != null) response.close();
                logger.info("URL is not available, sleeping for " + (SLEEP_DURATION/1000) + " seconds, retry count: " + i);
                Thread.sleep(SLEEP_DURATION);
                i++;
            }
        }
        
        if (i > RETRY_COUNT)
        {
            throw new IllegalStateException("URL '" + url + "' is not available");
        }
    }
}
