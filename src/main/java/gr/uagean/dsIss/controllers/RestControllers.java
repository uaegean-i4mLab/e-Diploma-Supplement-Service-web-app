/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uagean.dsIss.model.pojo.IssAttributeList;
import gr.uagean.dsIss.model.pojo.IssErrorResponse;
import gr.uagean.dsIss.model.pojo.ResponseForISS;
import gr.uagean.dsIss.service.EidasPropertiesService;
import gr.uagean.dsIss.utils.IssErrorMapper;
import gr.uagean.dsIss.utils.IssResponseParser;
import gr.uagean.dsIss.utils.Wrappers;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author nikos
 */
@Controller
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
public class RestControllers {

    Logger log = LoggerFactory.getLogger(RestControllers.class);
    private final static String SECRET = System.getenv("SP_SECRET");

    @Autowired
    private EidasPropertiesService propServ;

    @Autowired
    private CacheManager cacheManager;

    @Value("${eidas.error.consent}")
    private String EIDAS_CONSENT_ERROR;

    @Value("${eidas.error.qaa}")
    private String EIDAS_QAA_ERROR;

    @Value("${eidas.error.missing}")
    private String EIDAS_MISSING_ATTRIBUTE_ERROR;

    @RequestMapping("/attributeList")
    public @ResponseBody
    IssAttributeList getAttributeList() {
        return Wrappers.wrapEidasPropsToIssAttrs(propServ.getEidasProperties());
    }

    @RequestMapping(value = "/issResponse", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody
    ResponseForISS receiveIssResponse(
            @RequestParam(value = "r", required = false) String responseString,
            @RequestParam(value = "t", required = false) String token) {
        try {

            log.info("received the string: \n" + responseString);
            if (responseString.trim().equals("{}") || StringUtils.isEmpty(responseString.trim())
                    || (responseString.contains("StatusCode") && responseString.contains("StatusMessage"))) {
                log.info("Error Response");
                IssErrorResponse err = IssErrorMapper.wrapErrorToObject(responseString);
                if (err.getStatusMessage().getValue().contains("202007") || err.getStatusMessage().getValue().contains("202012")) {
                    cacheManager.getCache("errors").put(token, EIDAS_CONSENT_ERROR);
                }
                if (err.getStatusMessage().getValue().contains("202004")) {
                    cacheManager.getCache("errors").put(token, EIDAS_QAA_ERROR);
                }
                if (err.getStatusMessage().getValue().contains("202010")) {
                    cacheManager.getCache("errors").put(token, EIDAS_MISSING_ATTRIBUTE_ERROR);
                }
                cacheManager.getCache("tokens").put(token, responseString);
                return new ResponseForISS(false);
            }

            Map<String, String> jsonMap = IssResponseParser.parse(responseString);
            ObjectMapper mapper = new ObjectMapper();
            String access_token = Jwts.builder()
                    .setSubject(mapper.writeValueAsString(jsonMap))
                    .signWith(SignatureAlgorithm.HS256, SECRET.getBytes("UTF-8"))
                    .compact();

            cacheManager.getCache("tokens").put(token, access_token);
            return new ResponseForISS(true);
        } catch (IOException | IndexOutOfBoundsException e) {
            log.info("Error " + e.getStackTrace().toString());
            return new ResponseForISS(false);
        }
    }

}
