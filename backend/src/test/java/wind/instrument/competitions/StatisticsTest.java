package wind.instrument.competitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wind.instrument.competitions.rest.model.ActiveCompetitions;
import wind.instrument.competitions.rest.model.UserData;
import wind.instrument.competitions.rest.model.votestatistic.UserStatisticHistory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatisticsTest extends WindInstCompetitionApplicationTests {
    private  static Logger LOG = LoggerFactory.getLogger(StatisticsTest.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private WebResource service;
    private ResteasyClient resteasyClient;



    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        service = client.resource(UriBuilder.fromUri("http://localhost:8080").build());
        LoggingFilter loggingFilter = new  LoggingFilter(System.out);
        service.addFilter(loggingFilter);
        resteasyClient = new ResteasyClientBuilder().build();

    }

    private class JsonDateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String s = json.getAsJsonPrimitive().getAsString();
            long l = Long.parseLong(s);
            Date d = new Date(l);
            return d;
        }
    }

    //@Test
    public  void testMocMvcStatisticWithAuth() throws  Exception {
        mockMvc.perform(get("/api/votestatistic?cid=2").with(user("test").password("12345678")))
                .andExpect(status().isOk());
                //.andExpect(content().contentType("application/json;charset=UTF-8"))
                //.andExpect(jsonPath("$.allVoteItemIdList").exists())
                //.andExpect(jsonPath("$.voters").exists());
    }

    //@Test
    public void testAllStatistic() throws Exception {
        mockMvc.perform(get("/api/allstatistic")).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andDo((content) -> {
                    String json = content.getResponse().getContentAsString();
                    ObjectMapper mapper=new ObjectMapper();
                    List<UserStatisticHistory> hists =
                            mapper.readValue(json, new TypeReference<List<UserStatisticHistory>>(){});
                    for (UserStatisticHistory hist : hists) {
                        assertTrue("UserId is not set", hist.getUserId() != null);
                        assertTrue("Leaves is not set", hist.getLeaves() != null);
                        assertTrue("broomType is not set", hist.getBroomType() != null);
                        assertTrue("Competitions is not set", hist.getCompIds() != null);
                        assertTrue("No competitions returned", (hist.getCompIds() != null && hist.getCompIds().size() > 0));
                    }
                });


    }

    //@Test
    public void testJerseyRestClient() throws Exception {
        String json = service. path("api").path("allstatistic").accept(MediaType.APPLICATION_JSON).get(String.class);
        String schema = new String(Files.readAllBytes(Paths.get("D:\\projects\\JavaWebForum\\forum1\\backend\\src\\test\\java\\wind\\instrument\\competitions\\statistics.json")));
        //boolean isValid =
        //JSONValidator.validateJson(schema, json);
        assertTrue("JSON has not expected format:", JSONValidator.isJsonValid(schema, json, LOG));
    }



    @Test
    public void testRestEasyInObject() throws Exception {
        //ResteasyWebTarget target = resteasyClient.target(UriBuilder.fromPath("http://localhost:8080/api/getUserDetails?uid=1"));
        ClientRequest request = new ClientRequest("http://localhost:8080/api/getUserDetails?uid=1");
        request.accept("application/json");

        //Obtaining the client response
        ClientResponse json = request.get(UserData.class);
    }

    @Test
    public void testRestEasyInString() throws Exception {
        //ResteasyWebTarget target = resteasyClient.target(UriBuilder.fromPath("http://localhost:8080/api/getUserDetails?uid=1"));
        ClientRequest request = new ClientRequest("http://localhost:8080/api/getUserDetails?uid=1");
        request.accept("application/json");

        //Obtaining the client response
        ClientResponse json = request.get(String.class);
    }

    @Test
    public void testJerseyJsonStringGsonObjectXml() throws Exception {
        String json = service. path("api").path("getUserDetails").queryParam("uid", "1").accept(MediaType.APPLICATION_JSON).get(String.class);
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
        UserData userData = gson.fromJson(json, UserData.class);
        JAXBContext context = JAXBContext.newInstance(UserData.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(userData, sw);
        System.out.println(sw.toString());
    }

    @Test
    public void testJerseyJustString() throws Exception {
        String json = service. path("api").path("getUserDetails").queryParam("uid", "1").accept(MediaType.APPLICATION_JSON).get(String.class);
    }


    @Test
    public void testJerseyJsonObjectXml() throws Exception {
        UserData userData =service. path("api").path("getUserDetails").queryParam("uid", "1").accept(MediaType.APPLICATION_JSON).get(UserData.class);
        JAXBContext context = JAXBContext.newInstance(UserData.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(userData, sw);
        System.out.println(sw.toString());
    }

    @Test
    public  void testMocMvcJsonInString() throws  Exception {
         MvcResult result = mockMvc.perform(get("/api/getUserDetails?uid=1")).andExpect(status().isOk()).andReturn();
         String json = result.getResponse().getContentAsString();
    }

    //@Test
    public void testJerseyRestClientWithAuth() throws Exception {
        final HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter("test", "12345678");

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        client.addFilter(authFilter);

        WebResource service = client.resource(UriBuilder.fromUri("http://localhost:8080/api/votestatistic?cid=2").build());
        System.out.println(service.accept(MediaType.APPLICATION_JSON).get(String.class));
    }


}
