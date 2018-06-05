/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uagean.dsIss.controllers.ViewControllers;
import gr.uagean.dsIss.service.CountryService;
import gr.uagean.dsIss.service.EidasPropertiesService;
import gr.uagean.dsIss.service.KeyStoreService;
import gr.uagean.dsIss.service.ParameterService;
import gr.uagean.dsIss.utils.IssResponseParser;
import gr.uagean.dsIss.utils.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 *
 * @author nikos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class TestViewControllers {

    @Configuration
    static class SaveJSONPOSTConfig {

        @Bean
        public ViewControllers viewControllers() {
            return new ViewControllers();
        }

        @Bean
        public EidasPropertiesService propServ() {
            return Mockito.mock(EidasPropertiesService.class);
        }

        @Bean
        public CountryService countryServ() {
            return Mockito.mock(CountryService.class);
        }

        @Bean
        public CacheManager cacheManager() {
            return PowerMockito.mock(CacheManager.class);
        }

        @Bean
        public ParameterService paramServ() {
            return Mockito.mock(ParameterService.class);
        }

        @Bean
        public KeyStoreService keyServ() throws KeyStoreException, KeyStoreException, IOException, IOException, FileNotFoundException, NoSuchAlgorithmException, NoSuchAlgorithmException, NoSuchAlgorithmException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
            return Mockito.mock(KeyStoreService.class);
        }

    }

    @Autowired
    private ViewControllers viewControllers;

    @Autowired
    private EidasPropertiesService propServ;

    @Autowired
    private ParameterService paramServ;

    @Autowired
    private KeyStoreService keyServ;

    Cache.ValueWrapper mockWrapper;
    Cache.ValueWrapper jwtMockWrapper;

    @Autowired
    private CacheManager cacheManager;
    private Cache cache;
    private Cache jwtCache;

    @Before
    public void setup() throws UnsupportedEncodingException, JsonProcessingException, IndexOutOfBoundsException, IOException {
        List<String> eidasProps = new ArrayList();
        eidasProps.add("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier");
        eidasProps.add("http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName");
        Mockito.when(propServ.getEidasProperties())
                .thenReturn(eidasProps);

        String responseString = "{\"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName\":{\"value\":\"javier\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\":{\"value\":\"Garcia\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth\":{\"value\":\"1965-01-01\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\":{\"value\":\"GR/GR/12345\",\"complex\":\"0\",\"required\":\"1\"}}";
        String SECRET = "secret";
        Map<String, String> jsonMap = IssResponseParser.parse(responseString);
        ObjectMapper mapper = new ObjectMapper();
        String access_token = Jwts.builder()
                .setSubject(mapper.writeValueAsString(jsonMap))
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes("UTF-8"))
                .compact();

        cache = PowerMockito.mock(Cache.class);
        jwtCache = PowerMockito.mock(Cache.class);

        when(cacheManager.getCache("ips")).thenReturn(cache);
        when(cacheManager.getCache("tokens")).thenReturn(jwtCache);

        mockWrapper = () -> {
            return "localhost";
        };

        jwtMockWrapper = () -> {
            return access_token;
        };

        when(cache.get(any(String.class))).thenReturn(mockWrapper);
        when(jwtCache.get(any(String.class))).thenReturn(jwtMockWrapper);

        Mockito.when(paramServ.getParam("SP_SECRET")).thenReturn("secret");
        Mockito.when(paramServ.getParam("SP_SUCCESS_PAGE")).thenReturn("http://www.sp_success.com");
        Mockito.when(paramServ.getParam("ASYNC_SIGNATURE")).thenReturn("false");
    }

    @Test
    public void testISSwithHeaderJWT() throws Exception {
        String responseString = "{\"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName\":{\"value\":\"javier\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\":{\"value\":\"Garcia\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth\":{\"value\":\"1965-01-01\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\":{\"value\":\"GR/GR/12345\",\"complex\":\"0\",\"required\":\"1\"}}";
        Map<String, String> jsonMap = IssResponseParser.parse(responseString);
        String access_token = JwtUtils.getJWT(jsonMap, paramServ, keyServ,"eIDAS");

        Mockito.when(paramServ.getParam("HTTP_HEADER")).thenReturn("true");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.viewControllers).build();
        MvcResult result = mockMvc.perform(get("/authsuccess").param("t", "token1"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("http://www.sp_success.com"))
                .andReturn();
        assertEquals(cacheManager.getCache("tokens").get("token1").get(), access_token);

        String headerValue = result.getResponse().getHeader("Authorization");
        assertEquals(headerValue, access_token);

    }

    @Test
    public void testISSwithCookieJWT() throws Exception {
        String responseString = "{\"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName\":{\"value\":\"javier\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\":{\"value\":\"Garcia\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth\":{\"value\":\"1965-01-01\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\":{\"value\":\"GR/GR/12345\",\"complex\":\"0\",\"required\":\"1\"}}";
        Map<String, String> jsonMap = IssResponseParser.parse(responseString);
        String access_token = JwtUtils.getJWT(jsonMap, paramServ, keyServ,"eIDAS");

        Mockito.when(paramServ.getParam("HTTP_HEADER")).thenReturn("false");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(this.viewControllers).build();
        MvcResult result = mockMvc.perform(get("/authsuccess").param("t", "token1"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("http://www.sp_success.com"))
                .andReturn();
        assertEquals(cacheManager.getCache("tokens").get("token1").get(), access_token);

        String headerValue = result.getResponse().getHeader("Authorization");
        Cookie expectedCookie = result.getResponse().getCookie("access_token");
        assertEquals(headerValue, null);
        assertEquals(expectedCookie.getValue(), access_token);

    }

}
