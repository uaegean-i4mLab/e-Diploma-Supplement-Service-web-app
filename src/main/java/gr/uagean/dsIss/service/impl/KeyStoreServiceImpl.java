/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service.impl;

import gr.uagean.dsIss.service.KeyStoreService;
import gr.uagean.dsIss.service.ParameterService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author nikos
 */
@Service
public class KeyStoreServiceImpl implements KeyStoreService {

    private final String jwtCertPath;
    private final String keyPass;
    private final String storePass;
    private final String keyAlias;

    private KeyStore keystore;

    private ParameterService paramServ;

    @Autowired
    public KeyStoreServiceImpl(ParameterService paramServ) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        this.paramServ = paramServ;
        jwtCertPath = this.paramServ.getParam("SP_JWT_CERT");
        keyPass = this.paramServ.getParam("KEY_PASS");
        storePass = this.paramServ.getParam("STORE_PASS");
        keyAlias = this.paramServ.getParam("CERT_ALIAS");

        keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        if (!StringUtils.isEmpty(paramServ.getParam("ASYNC_SIGNATURE")) && Boolean.parseBoolean(paramServ.getParam("ASYNC_SIGNATURE"))) {
            File jwtCertFile = new File(jwtCertPath);
            InputStream certIS = new FileInputStream(jwtCertFile);
            keystore.load(certIS, storePass.toCharArray());
        }

    }

    public Key getJWTSigningKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        //"jwtkey"
        //return keystore.getKey(keyAlias, "keypassword".toCharArray());
        return keystore.getKey(keyAlias, keyPass.toCharArray());
    }

    public PublicKey getJWTPublicKey() throws KeyStoreException {
        //"jwtkey"
        Certificate cert = keystore.getCertificate(keyAlias);
        return cert.getPublicKey();
    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStore keystore) {
        this.keystore = keystore;
    }

    public ParameterService getParamServ() {
        return paramServ;
    }

    public void setParamServ(ParameterService paramServ) {
        this.paramServ = paramServ;
    }

}
