package org.alfresco.deployment.appTest;

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

    /**
     * Test to check if we pass a invalid app like just the url without body it does not give 200 status
     * 
     * @throws Exception
     */
    @Test(priority = 0)
    public void invalidAppRequestURL() throws Exception
    {
        logger.info("Test to validate the rest request for the following app :" + appUrl);
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUrl);
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertFalse((response.getStatusLine().getStatusCode() == 200),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
    }

    /**
     * Test case to validate post a valid get app request the response code is correct and json object is correct.
     * 
     * @throws Exception
     * @author sprasanna
     */
    @Test(priority = 1)
    public void validAppRestRequest() throws Exception
    {
        logger.info("Test to validate the rest request for the following app :" + appUrl + "/welcome");
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUrl + "/welcome");
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 200),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        String jsonOutput = jsonResponse(response);
        Assert.assertTrue((jsonOutput.equals("Hello World!")), String.format("The json object value [%s] is not matching", jsonOutput));
    }

    /**
     * Test case to validate post a valid get app request the response code is correct and json object is correct.
     * 
     * @throws Exception
     */
    @Test(priority = 2)
    public void validCreateRequest() throws Exception
    {
        StringEntity params = new StringEntity("{\"key\":\"test\",\"value\":\"substest!\"} ");
        logger.info("POST request");
        client = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(appUrl);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(params);
        response = (CloseableHttpResponse) client.execute(postRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 201),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
    }

    /**
     * Test case to validate get of previous posted request response code is correct and json object is correct.
     * 
     * @throws Exception
     */

    @Test(priority = 3, dependsOnMethods = { "validCreateRequest" })
    public void validGetOfCreateRequest() throws Exception
    {
        logger.info("Get for the previous posted request " + appUrl + "/test");
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUrl + "/test");
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 200),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        String jsonOutput = jsonResponse(response);
        Assert.assertTrue((jsonOutput.equals("substest!")), String.format("The json object value [%s] is not matching", jsonOutput));
    }

    /**
     * Test case to validate update case
     * 
     * @throws Exception
     * @author sprasanna
     */
    @Test(priority = 4)
    public void validUpdateRequest() throws Exception
    {
        StringEntity params = new StringEntity("{\"key\":\"test\",\"value\":\"substestUpdate!\"} ");
        logger.info("Update request " + appUrl + "/test");
        client = HttpClientBuilder.create().build();
        HttpPut postRequest = new HttpPut(appUrl + "/test");
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(params);
        response = (CloseableHttpResponse) client.execute(postRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 200),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
    }

    /**
     * Test case to validate get updateCase
     * 
     * @throws Exception
     */

    @Test(priority = 5, dependsOnMethods = { "validUpdateRequest" })
    public void validGetOfUpdateRequest() throws Exception
    {
        logger.info("Get for the previous updated request " + appUrl + "/test");
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUrl + "/test");
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 200),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
        String jsonOutput = jsonResponse(response);
        Assert.assertTrue((jsonOutput.equals("substestUpdate!")), String.format("The json object value [%s] is not matching", jsonOutput));
    }

    /**
     * Test case to validate delete
     * 
     * @throws Exception
     */
    @Test(priority = 6)
    public void validDeleteRequest() throws Exception
    {
        logger.info("Delete Request " + appUrl + "/test");
        client = HttpClientBuilder.create().build();
        HttpDelete postRequest = new HttpDelete(appUrl + "/test");
        response = (CloseableHttpResponse) client.execute(postRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 204),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
    }

    /**
     * Test case to validate get delete.
     * 
     * @throws Exception
     */

    @Test(priority = 7, dependsOnMethods = { "validDeleteRequest" })
    public void validGetOfDeleteRequest() throws Exception
    {
        logger.info("get Request for the deleted data " + appUrl + "/test");
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUrl + "/test");
        response = (CloseableHttpResponse) client.execute(getRequest);
        Assert.assertTrue((response.getStatusLine().getStatusCode() == 404),
                String.format("The response code [%s] is incorrect", response.getStatusLine().getStatusCode()));
    }

    /**
     * Method which will retrive data from the response for the given value
     * @param response
     * @return
     * @throws Exception
     */
    
    private String jsonResponse(CloseableHttpResponse response) throws Exception
    {
        String json_string = EntityUtils.toString(response.getEntity());
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json_string);
        return ((String) obj.get("value"));
    }

    @AfterMethod
    private void closeResponse() throws Exception
    {
        response.close();
    }

}
