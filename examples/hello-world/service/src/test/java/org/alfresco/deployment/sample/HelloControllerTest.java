package org.alfresco.deployment.sample;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    private String content = "{\"key\":\"test\",\"value\":\"Hello World!\"}";
    private String updatedContent = "{\"key\":\"test\",\"value\":\"Hello Test!\"}";

    @Test
    public void testWelcomeMessage() throws Exception
    {
        // ensure the welcome message is present
        mvc.perform(MockMvcRequestBuilders.get("/hello/welcome")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello World!")));
    }

    @Test
    public void testHelloAPI() throws Exception
    {
        // create a test message
        mvc.perform(MockMvcRequestBuilders.post("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().string(equalTo(content)));

        // retrieve the test message
        mvc.perform(MockMvcRequestBuilders.get("/hello/test")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(content)));

        // update the test message
        mvc.perform(MockMvcRequestBuilders.put("/hello/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedContent)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(updatedContent)));

        // retrieve the updated message
        mvc.perform(MockMvcRequestBuilders.get("/hello/test")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(updatedContent)));

        // delete the test message
        mvc.perform(MockMvcRequestBuilders.delete("/hello/test")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // ensure that the test message now returns 404
        mvc.perform(MockMvcRequestBuilders.get("/hello/test")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
