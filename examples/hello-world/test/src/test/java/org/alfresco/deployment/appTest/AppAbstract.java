package org.alfresco.deployment.appTest;

import java.net.URL;
import java.util.Properties;

import org.testng.annotations.BeforeSuite;

public class AppAbstract
{
    protected static String appServicesUrl;
    protected static String appUIUrl;
    Properties appProperty = new Properties();
    protected static URL url ;

    /**
     * The before suit will load test properties file and load the same.
     */
    
    @BeforeSuite 
    public void initialSetup() throws Exception
    {
            appProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
            appServicesUrl = readProperty("app.services.url");
            appUIUrl=readProperty("app.ui.url");
            if(appServicesUrl.isEmpty() || !(appServicesUrl.contains("hello"))|| (appUIUrl.isEmpty()))
            {
            	throw new Exception("please set the app-url details in the properties file");
            }
            url = new URL(appServicesUrl);
        
    }
    
    private String readProperty(String propertyType)
    {
        return appProperty.getProperty(propertyType);
    }
}
