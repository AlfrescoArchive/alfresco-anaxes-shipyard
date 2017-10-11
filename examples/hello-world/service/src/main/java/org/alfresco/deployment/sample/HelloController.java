package org.alfresco.deployment.sample;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/hello")
public class HelloController
{
    @Autowired
    private HelloTextService helloTextService;

    @RequestMapping(path = "/{key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public HelloTextModel getHelloText(@PathVariable String key)
    {
        return helloTextService.get(key);
    }

    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public HelloTextModel createHelloText(@RequestBody HelloTextModel helloText)
    {
        return helloTextService.create(helloText);
    }

    @RequestMapping(path = "/{key}", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    public HelloTextModel updateHelloText(@RequestBody HelloTextModel helloText)
    {
        return helloTextService.update(helloText);
    }

    @RequestMapping(path = "/{key}", method = RequestMethod.DELETE)
    public void updateHelloText(@PathVariable String key)
    {
        helloTextService.delete(key);
    }
}
