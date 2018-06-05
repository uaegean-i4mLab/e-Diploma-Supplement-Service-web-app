/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uagean.dsIss.service.KeyStoreService;
import gr.uagean.dsIss.service.ParameterService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.UnsupportedEncodingException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Map;

/**
 *
 * @author nikos
 */
public class JwtUtils {

    public static String getJWT(Map<String, String> jsonMap, ParameterService paramServ, KeyStoreService keyServ, String origin)
            throws JsonProcessingException, UnsupportedEncodingException, KeyStoreException,
            NoSuchAlgorithmException, NoSuchAlgorithmException, UnrecoverableKeyException {
        ObjectMapper mapper = new ObjectMapper();
        
        JwtBuilder builder = Jwts.builder()
                .setSubject(mapper.writeValueAsString(jsonMap))
                .claim("origin", origin);

        if (paramServ.getParam("ASYNC_SIGNATURE") != null && Boolean.parseBoolean(paramServ.getParam("ASYNC_SIGNATURE"))) {
            builder.signWith(SignatureAlgorithm.RS256, keyServ.getJWTSigningKey());
        } else {
            builder.signWith(SignatureAlgorithm.HS256, paramServ.getParam("SP_SECRET").getBytes("UTF-8"));
        }

        return builder.compact();
    }
    
    
    

}
