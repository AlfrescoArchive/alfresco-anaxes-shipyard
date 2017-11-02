package org.alfresco.deployment.sample;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

  @Autowired private MockMvc mvc;

  private String content = "{\"key\":\"test\",\"value\":\"Hello World!\"}";
  private String updatedContent = "{\"key\":\"test\",\"value\":\"Hello Test!\"}";
  private String ORIGIN_URL = "http://localhost:4200";

  @Test
  public void testWelcomeMessage() throws Exception {
    // ensure the welcome message is present
    mvc.perform(
            MockMvcRequestBuilders.get("/hello/welcome")
                .accept(MediaType.APPLICATION_JSON)
                .header(ACCESS_CONTROL_REQUEST_METHOD, GET)
                .header(ORIGIN, ORIGIN_URL))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Hello World!")))
        .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN_URL));
  }

  @Test
  public void testHelloAPI() throws Exception {
    // create a test message
    mvc.perform(
            MockMvcRequestBuilders.post("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON)
                .header(ACCESS_CONTROL_REQUEST_METHOD, POST)
                .header(ORIGIN, ORIGIN_URL))
        .andExpect(status().isCreated())
        .andExpect(content().string(equalTo(content)))
        .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN_URL));

    // retrieve the test message
    mvc.perform(
            MockMvcRequestBuilders.get("/hello/test")
                .accept(MediaType.APPLICATION_JSON)
                .header(ACCESS_CONTROL_REQUEST_METHOD, GET)
                .header(ORIGIN, ORIGIN_URL))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(content)))
        .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN_URL));

    // update the test message
    mvc.perform(
            MockMvcRequestBuilders.put("/hello/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedContent)
                .accept(MediaType.APPLICATION_JSON)
                .header(ACCESS_CONTROL_REQUEST_METHOD, PUT)
                .header(ORIGIN, ORIGIN_URL))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(updatedContent)))
        .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN_URL));

    // retrieve the updated message
    mvc.perform(
            MockMvcRequestBuilders.get("/hello/test")
                .accept(MediaType.APPLICATION_JSON)
                .header(ACCESS_CONTROL_REQUEST_METHOD, GET)
                .header(ORIGIN, ORIGIN_URL))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(updatedContent)))
        .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN_URL));

    // delete the test message
    mvc.perform(
            MockMvcRequestBuilders.delete("/hello/test")
                .accept(MediaType.APPLICATION_JSON)
                .header(ACCESS_CONTROL_REQUEST_METHOD, DELETE)
                .header(ORIGIN, ORIGIN_URL))
        .andExpect(status().isNoContent())
        .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN_URL));

    // ensure that the test message now returns 404
    mvc.perform(
            MockMvcRequestBuilders.get("/hello/test")
                .accept(MediaType.APPLICATION_JSON)
                .header(ACCESS_CONTROL_REQUEST_METHOD, GET)
                .header(ORIGIN, ORIGIN_URL))
        .andExpect(status().isNotFound())
        .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN_URL));
  }
}
