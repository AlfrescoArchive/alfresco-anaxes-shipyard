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

import java.net.URL;
import java.util.Properties;

import org.testng.annotations.BeforeSuite;

public class AppAbstract
{
    protected static String appUrl;
    Properties appProperty = new Properties();
    protected static URL url ;

    /**
     * The before suit will load test properties file and load the same.
     */
    @BeforeSuite 
    public void initialSetup() throws Exception
    {
        appProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
        appUrl = readProperty("app.url");
        if(appUrl.isEmpty() || !(appUrl.contains("hello")))
        {
            throw new Exception("please set the app-url details in the properties file");
        }
        url = new URL(appUrl);
    }
    
    private String readProperty(String propertyType)
    {
        return appProperty.getProperty(propertyType);
    }
}
