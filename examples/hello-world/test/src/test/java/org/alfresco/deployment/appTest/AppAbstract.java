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
