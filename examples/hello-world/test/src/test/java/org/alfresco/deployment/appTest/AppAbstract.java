package org.alfresco.deployment.appTest;

import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeSuite;

public class AppAbstract
{
    protected static String appUrl;
    Properties appProperty = new Properties();
    private static  Log logger = LogFactory.getLog(AppAbstract.class);
    protected static URL url ;

    /**
     * The before suit will load test properties file and load the same.
     */
    
    @BeforeSuite 
    public void initialSetup() 
    {
        try
        {
            appProperty.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
            appUrl = readProperty("app.url");
            url = new URL(appUrl);
        }
        catch (Exception e)
        {
            logger.error("Failed to load  App properties  :" + this.getClass(), e);
        }
        
    }
    
    private String readProperty(String propertyType)
    {
        return appProperty.getProperty(propertyType);
    }
}
