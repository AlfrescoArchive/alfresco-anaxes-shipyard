package org.alfresco.deployment.sample;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/hello")
public class HelloController
{
    @Autowired
    private HelloTextRepository helloTextRepository;

    @RequestMapping(path = "/{key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HelloText> getHelloText(@PathVariable String key)
    {
        HelloText helloText = helloTextRepository.findOne(key);
        if (helloText == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HelloText>(helloText, HttpStatus.OK);
                
    }

    @RequestMapping(method = RequestMethod.POST, 
            consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<HelloText> createHelloText(@RequestBody HelloText helloText)
    {
        return new ResponseEntity<HelloText>(helloTextRepository.save(helloText), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{key}", method = RequestMethod.PUT, 
            consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<HelloText> updateHelloText(@RequestBody HelloText helloText)
    {
        return new ResponseEntity<HelloText>(helloTextRepository.save(helloText), HttpStatus.OK);
    }

    @RequestMapping(path = "/{key}", method = RequestMethod.DELETE)
    public ResponseEntity<?> updateHelloText(@PathVariable String key)
    {
        try
        {
            helloTextRepository.delete(key);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); 
        }
    }
}