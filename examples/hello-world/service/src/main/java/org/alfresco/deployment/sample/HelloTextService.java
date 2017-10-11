package org.alfresco.deployment.sample;

public interface HelloTextService
{
    public HelloTextModel get(String key);

    public HelloTextModel create(HelloTextModel helloText);

    public HelloTextModel update(HelloTextModel helloText);

    public void delete(String key);
}
