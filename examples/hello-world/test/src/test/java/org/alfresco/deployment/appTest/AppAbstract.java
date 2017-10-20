package org.alfresco.deployment.appTest;

import java.util.Properties;

import org.testng.annotations.BeforeSuite;

public class AppAbstract
{
    protected static String appServicesUrl;
    private static String appUrl;
    protected static String appUIUrl;
    Properties appProperty = new Properties();

    /**
     * The before suit will load test properties file and load the same.
     */
    
    @BeforeSuite 
    public void initialSetup() throws Exception
    {
            appProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
            appUrl = readProperty("app.url");
            if(appUrl.isEmpty())
            {
            	throw new Exception("please set the app-url details in the properties file");
            }
            appServicesUrl = appUrl + "/" + "hello";
            appUIUrl = appUrl;
        
    }
    
    private String readProperty(String propertyType)
    {
        return appProperty.getProperty(propertyType);
    }
}
