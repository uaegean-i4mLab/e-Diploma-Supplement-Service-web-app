/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uagean.dsIss.controllers.RestControllers;
import gr.uagean.dsIss.model.pojo.ResponseForISS;
import gr.uagean.dsIss.service.EidasPropertiesService;
import gr.uagean.dsIss.utils.IssResponseParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 *
 * @author nikos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class TestRestControllers {

    @Configuration
    static class SaveJSONPOSTConfig {

        @Bean
        public RestControllers restControllers() {
            return new RestControllers();
        }

        @Bean
        public EidasPropertiesService propServ() {
            return Mockito.mock(EidasPropertiesService.class);
        }

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("tokens");
        }
    }

    @Autowired
    private RestControllers restControllers;

    @Autowired
    private EidasPropertiesService propServ;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void setup() {
        List<String> eidasProps = new ArrayList();
        eidasProps.add("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier");
        eidasProps.add("http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName");
        Mockito.when(propServ.getEidasProperties())
                .thenReturn(eidasProps);
    }

    @Test
    public void testWhenWithCookies() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.restControllers).build();
        MvcResult result = mockMvc.perform(get("/attributeList"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        assertEquals("{\"status\":\"OK\",\"list\":{\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\":{\"value\":null,\"complex\":0,\"required\":1},\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\":{\"value\":null,\"complex\":0,\"required\":1}}}",
                content);
    }

    @Test
    public void testSaveIssResponse() throws Exception {
        String responseString = "{\"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName\":{\"value\":\"javier\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\":{\"value\":\"Garcia\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth\":{\"value\":\"1965-01-01\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\":{\"value\":\"GR/GR/12345\",\"complex\":\"0\",\"required\":\"1\"}}";
        String SECRET = "secret";
        Map<String, String> jsonMap = IssResponseParser.parse(responseString);
        ObjectMapper mapper = new ObjectMapper();
        String access_token = Jwts.builder()
                .setSubject(mapper.writeValueAsString(jsonMap))
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes("UTF-8"))
                .compact();

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.restControllers).build();
        MvcResult result = mockMvc.perform(post("/issResponse").param("r", responseString).param("t", "token1"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(cacheManager.getCache("tokens").get("token1").get(), access_token);
        assertEquals(result.getResponse().getContentAsString(), "{\"status\":\"OK\"}");
    }
}
