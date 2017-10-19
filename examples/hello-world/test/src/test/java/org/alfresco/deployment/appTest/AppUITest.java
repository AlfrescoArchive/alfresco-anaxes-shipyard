package org.alfresco.deployment.appTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AppUITest extends AppAbstract
{
    private static Log logger = LogFactory.getLog(AppUITest.class);

    /**
     * Test to check the UI response is correct
     * @throws Exception
     * @throws
     */
    @Test
    public void testApp() throws Exception
    { 
        CloseableHttpClient client = null;
        CloseableHttpResponse response= null;
        BufferedReader rd = null ;
        try
        {
        logger.info("Test the UI is working correctly");
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUIUrl);
        response = client.execute(getRequest);
        rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null)
        {
            result.append(line);
        }
        Assert.assertFalse(result.toString().contains("error"), String.format("The page is not loaded correctly it contains error"));
        Assert.assertTrue(result.toString().contains("<title>Demo Application</title>"), String.format("The title is not displayed correctly"));
    }
     finally
     {
         rd.close();
         response.close();
         client.close();
     }
   }
}
