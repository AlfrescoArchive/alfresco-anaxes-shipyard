package org.alfresco.deployment.sample;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloService
{
    @RequestMapping("/hello")
    public String hello()
    {
        return "{ \"test\": \"Hello World!\" }";
    }
}
