package org.alfresco.deployment.sample;

import java.util.HashMap;

/**
 * In-memory Implementation of hello text persistence
 */
public class HelloTextServiceInMemoryImpl implements HelloTextService
{
    private HashMap<String, String> helloTextData;
    
    public HelloTextServiceInMemoryImpl()
    {
        helloTextData = new HashMap<String, String>(5);
        helloTextData.put("test", "Hello World!");
    }
    
    @Override
    public HelloTextModel get(String key)
    {
        String value = helloTextData.get(key);
        if (value == null)
        {
            return null;
        }
        return new HelloTextModel(key, value);
    }

    @Override
    public HelloTextModel create(HelloTextModel helloText)
    {
        helloTextData.put(helloText.getKey(), helloText.getValue());
        return get(helloText.getKey());
    }
    
    @Override
    public HelloTextModel update(HelloTextModel helloText)
    {
        helloTextData.put(helloText.getKey(), helloText.getValue());
        return get(helloText.getKey());
    }

    @Override
    public void delete(String key)
    {
        if (helloTextData.containsKey(key))
        {
            helloTextData.remove(key);
        }
    }
}
