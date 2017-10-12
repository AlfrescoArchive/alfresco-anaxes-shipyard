package org.alfresco.deployment.sample;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class HelloText
{
    @Id
    private String key;
    private String value;
    
    public HelloText()
    {
    }
    
    public HelloText(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
    
    public String getKey()
    {
        return key;
    }
    public void setKey(String key)
    {
        this.key = key;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
}
