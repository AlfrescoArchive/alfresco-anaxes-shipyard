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
    private String key;
    private String value;

    /**
     * Test to check if we pass a invalid app like just the url without body it
     * does not give 200 status
     * 
     * @throws Exception
     */
    @Test(priority = 0)
    public void testInvalidAppRequestURL() throws Exception
    {
        logger.info("Test to validate the rest request for the following app :" + appUrl);
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUrl);
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
    @Test(priority = 1)
    public void testValidAppRequestURL() throws Exception
    {
        logger.info("Test to validate the rest request for the following app :" + appUrl + "/welcome");
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUrl + File.separator + "welcome");
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 200),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        String jsonOutput = extractValue(response);
        Assert.assertTrue((jsonOutput.equals("Hello World!")), String.format("The json object value [%s] is not matching", jsonOutput));
    }

    /**
     * Test case to validate post, put and delete request 
     * As part of the test we will create a key , and use the same key 
     * to test put and delete.  
     * 
     * @throws Exception
     */
    @Test(priority = 2)
    public void testHelloWorldAPI() throws Exception
    {
        HttpGet getRequest;
        StringEntity jsonBody ;
        logger.info("Create request");
        jsonBody = new StringEntity(generateJsonBody());
        client = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(appUrl);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(jsonBody);
        response = (CloseableHttpResponse) client.execute(postRequest);
        validateResponse(key, value, response,201);
        
        logger.info("Get request for created content");
        getRequest = new HttpGet(appUrl + File.separator + key);
        response = (CloseableHttpResponse) client.execute(getRequest);
        validateResponse(key, value, response,200);
        
        logger.info("Update request for the same key " + key);
        value = RandomStringUtils.randomAlphanumeric(4);
        String entityValue = "{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}";
        jsonBody =  new StringEntity(entityValue);
        HttpPut putRequest = new HttpPut(appUrl + File.separator + key);
        putRequest.setHeader("Content-Type", "application/json");
        putRequest.setEntity(jsonBody);
        response = (CloseableHttpResponse) client.execute(putRequest);
        validateResponse(key, value, response,200);
        
        logger.info("Get request for updated content");
        getRequest = new HttpGet(appUrl + File.separator + key);
        response = (CloseableHttpResponse) client.execute(getRequest);
        validateResponse(key, value, response,200);
        
        logger.info("delete request for the same key " + key);
        HttpDelete deleteRequest = new HttpDelete(appUrl + File.separator + key);
        response = (CloseableHttpResponse) client.execute(deleteRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 204),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        
        logger.info("Get request for put content");
        getRequest = new HttpGet(appUrl + File.separator + key);
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 404),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
    }


    /**
     * Method which will retrive data from the response for the given value
     * 
     * @param response
     * @return
     * @throws Exception
     */

    private String extractValue(CloseableHttpResponse response) throws Exception
    {
        String json_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json_string);
        return ((String) obj.get("value"));
    }

    @AfterMethod
    private void closeResponse() throws Exception
    {
        if (response != null)
        {
            response.close();
        }
    }

    private String generateJsonBody()
    {
        key = RandomStringUtils.randomAlphanumeric(4);
        value = RandomStringUtils.randomAlphanumeric(4);
        return ("{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}");
    }

    /**
     * Test case to validate get of previous posted request response code is
     * correct and json object is correct.
     * 
     * @throws Exception
     */

    private void validateResponse(String Key, String Value, CloseableHttpResponse response, int statusCode) throws Exception
    {
        Assert.assertTrue((response.getStatusLine().getStatusCode() == statusCode),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        String outputValue = extractValue(response);
        Assert.assertTrue((outputValue.equals(value)), String.format("The json object value [%s] is not matching", outputValue));
    }

}
