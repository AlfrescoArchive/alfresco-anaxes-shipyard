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
        logger.info("Test the UI is working correctly for the url "+ appUrl);
        client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(appUrl);
        response = client.execute(getRequest);
        rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null)
        {
            result.append(line);
        }
        String htmlOutput = result.toString();
        Assert.assertFalse(htmlOutput.contains("error"), String.format("The page is not loaded correctly it contains error [%s]", htmlOutput));
        Assert.assertTrue(htmlOutput.contains("<title>Demo Application</title>"), String.format("The title is not displayed correctly and the result is [%s]",htmlOutput));
    }
     finally
     {
         rd.close();
         response.close();
         client.close();
     }
   }
}
