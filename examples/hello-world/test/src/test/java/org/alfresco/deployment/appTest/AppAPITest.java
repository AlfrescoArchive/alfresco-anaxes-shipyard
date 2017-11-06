/*
 * Copyright 2017 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.deployment.appTest;

import java.io.File;

import org.apache.commons.lang.RandomStringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * regression test for sample App to validate the api request.
 */
public class AppAPITest extends AppAbstract
{
    private static Log logger = LogFactory.getLog(AppAPITest.class);
    private CloseableHttpClient client;
    private CloseableHttpResponse response;
    
    private String restApiUrl;

    @BeforeSuite
    public void setup() throws Exception
    {
        // do common setup
        commonSetup();
        
        // get the appropriate URL
        if (isMinikubeCluster())
        {
            restApiUrl = getUrlForMinikube("backend");
        }
        else
        {
            restApiUrl = getUrlForAWS("backend");
            
            // wait for the URL to become available
            waitForURL(restApiUrl);
        }
        
        // add the /hello to the base url
        StringBuffer buffer = new StringBuffer(restApiUrl);
        if (!restApiUrl.endsWith("/"))
        {
            buffer.append("/");
        }
        buffer.append("hello");
        restApiUrl = buffer.toString();
        
        logger.info("Testing REST API URL:" + restApiUrl);
    }

    /**
     * Test to check if we pass a invalid app like just the url without body it
     * does not give 200 status
     * 
     * @throws Exception
     */
    @Test(priority=0)
    public void testInvalidApiRequest() throws Exception
    {
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(restApiUrl);
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertFalse((response.getStatusLine().getStatusCode() == 200),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
    }

    /**
     * Test case to validate post a valid get app request the response code is
     * correct and json object is correct.
     * 
     * @throws Exception
     * @author sprasanna
     */
    @Test(priority=1)
    public void testValidApiRequest() throws Exception
    {
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(restApiUrl + File.separator + "welcome");
        response = (CloseableHttpResponse) client.execute(getRequest);
        validateResponse("welcome", "Hello World!", response, 200);
    }

    /**
     * Test case to validate post, put and delete request
     * As part of the test we will create a key , and use the same key
     * to test put and delete.
     * 
     * @throws Exception
     */
    @Test(priority=2)
    public void testHelloWorldApiRequest() throws Exception
    {
        HttpGet getRequest;
        StringEntity jsonBody;
        String key = RandomStringUtils.randomAlphanumeric(4);
        String value = RandomStringUtils.randomAlphanumeric(4);
        
        // test message creation
        jsonBody = new StringEntity(generateJsonBody(key, value));
        client = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(restApiUrl);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(jsonBody);
        response = (CloseableHttpResponse) client.execute(postRequest);
        validateResponse(key, value, response, 201);
        closeResponse();

        // retrieve new message
        getRequest = new HttpGet(restApiUrl + File.separator + key);
        response = (CloseableHttpResponse) client.execute(getRequest);
        validateResponse(key, value, response, 200);
        closeResponse();

        // update message
        value = RandomStringUtils.randomAlphanumeric(4);
        jsonBody = new StringEntity(generateJsonBody(key, value));
        HttpPut putRequest = new HttpPut(restApiUrl + File.separator + key);
        putRequest.setHeader("Content-Type", "application/json");
        putRequest.setEntity(jsonBody);
        response = (CloseableHttpResponse) client.execute(putRequest);
        validateResponse(key, value, response, 200);
        closeResponse();

        // get updated message
        getRequest = new HttpGet(restApiUrl + File.separator + key);
        response = (CloseableHttpResponse) client.execute(getRequest);
        validateResponse(key, value, response, 200);
        closeResponse();

        // delete message
        HttpDelete deleteRequest = new HttpDelete(restApiUrl + File.separator + key);
        response = (CloseableHttpResponse) client.execute(deleteRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 204),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        closeResponse();

        // make sure message has been deleted
        getRequest = new HttpGet(restApiUrl + File.separator + key);
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 404),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        closeResponse();
    }

    /**
     * Method which will retrive data from the response for the given value
     * 
     * @param response
     * @return
     * @throws Exception
     */
    private void closeResponse() throws Exception
    {
        if (response != null)
        {
            response.close();
        }
    }

    private String generateJsonBody(String key, String value)
    {
        return ("{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}");
    }

    /**
     * Test case to validate get of previous posted request response code is
     * correct and json object is correct.
     * 
     * @throws Exception
     */
    private void validateResponse(String key, String value, CloseableHttpResponse response, int statusCode) throws Exception
    {

        String json_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json_string);
        String valueOutput = (String) obj.get("value");
        String keyOutput = (String) obj.get("key");
        Assert.assertTrue((response.getStatusLine().getStatusCode() == statusCode),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        Assert.assertTrue((valueOutput.equals(value)), String.format("The json object value [%s] is not matching", valueOutput));
        Assert.assertTrue((keyOutput.equals(key)), String.format("The json object key [%s] is not matching", keyOutput));
    }

    @AfterMethod
    private void closeClient() throws Exception
    {
        if (client != null)
        {
            client.close();
        }
    }

}
