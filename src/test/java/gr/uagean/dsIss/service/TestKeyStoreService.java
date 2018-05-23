/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.uagean.dsIss.service.KeyStoreService;
import gr.uagean.dsIss.service.ParameterService;
import gr.uagean.dsIss.service.impl.KeyStoreServiceImpl;
import gr.uagean.dsIss.service.impl.KeyStoreServiceImpl;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.junit.Assert;
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
public class TestKeyStoreService {

    private Key key;

    KeyStoreService keyServ;

    @Mock
    ParameterService paramServ;

    @Before
    public void before() throws JsonProcessingException, IOException, KeyStoreException, UnsupportedEncodingException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, NoSuchAlgorithmException, CertificateException {

        ClassLoader classLoader = getClass().getClassLoader();
        String path = classLoader.getResource("testKeys/server.jks").getPath();

        Mockito.when(paramServ.getParam("SP_JWT_CERT")).thenReturn(path);
        Mockito.when(paramServ.getParam("KEY_PASS")).thenReturn("keypassword");
        Mockito.when(paramServ.getParam("STORE_PASS")).thenReturn("jkspassword");
        Mockito.when(paramServ.getParam("CERT_ALIAS")).thenReturn("jwtkey");
        Mockito.when(paramServ.getParam("ASYNC_SIGNATURE")).thenReturn("true");

        keyServ = new KeyStoreServiceImpl(paramServ);
    }

    @Test
    public void testGetJWTSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {

        Key key = keyServ.getJWTSigningKey();
        assertEquals(true, true);
        Assert.assertNotNull(key);
    }

    @Test
    public void testgetJWTPublicKey() throws KeyStoreException {

        PublicKey key = keyServ.getJWTPublicKey();
        Assert.assertNotNull(key);
        assertEquals(true, true);
    }

}
