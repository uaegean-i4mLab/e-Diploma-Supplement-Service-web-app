/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.uagean.dsIss.service.KeyStoreService;
import gr.uagean.dsIss.service.ParameterService;
import gr.uagean.dsIss.service.impl.KeyStoreServiceImpl;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author nikos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestJwtUtils {

    KeyStoreService keyServ;

    @Mock
    ParameterService paramServ;

    @Before
    public void before() throws JsonProcessingException, IOException, KeyStoreException, UnsupportedEncodingException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, NoSuchAlgorithmException, CertificateException {

        ClassLoader classLoader = getClass().getClassLoader();
        String path = classLoader.getResource("testKeys/test2/keystore.jks").getPath();
        Mockito.when(paramServ.getParam("SP_JWT_CERT")).thenReturn(path);
        Mockito.when(paramServ.getParam("KEY_PASS")).thenReturn("selfsignedpass");  // keypassword
        Mockito.when(paramServ.getParam("STORE_PASS")).thenReturn("keystorepass"); // jkspassword
        Mockito.when(paramServ.getParam("CERT_ALIAS")).thenReturn("selfsigned");
        Mockito.when(paramServ.getParam("ASYNC_SIGNATURE")).thenReturn("true");
        keyServ = new KeyStoreServiceImpl(paramServ);
    }

    @Test
    public void testGetJWtRSA() throws JsonProcessingException, JsonProcessingException,
            UnsupportedEncodingException, UnsupportedEncodingException, UnsupportedEncodingException,
            UnsupportedEncodingException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("name", "nikos");

        String json = JwtUtils.getJWT(jsonMap, paramServ, keyServ,"origin");
        assertEquals(true, true);
    }

    @Test
    public void testDecryptJwtRSA() throws JsonProcessingException, UnsupportedEncodingException,
            KeyStoreException, NoSuchAlgorithmException, NoSuchAlgorithmException, UnrecoverableKeyException {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("name", "nikos");
        String compactJws = JwtUtils.getJWT(jsonMap, paramServ, keyServ,"myOrigin");
        PublicKey key = keyServ.getJWTPublicKey();
        //term signing key here is confusing... we decrypt with the public key ;)
        String decryptedSubject = Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject();
        String decryptedOriginClaim = Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().get("origin", String.class);
        
        assertEquals(decryptedSubject,"{\"name\":\"nikos\"}");
        assertEquals(decryptedOriginClaim,"myOrigin");
        

    }
 

}
