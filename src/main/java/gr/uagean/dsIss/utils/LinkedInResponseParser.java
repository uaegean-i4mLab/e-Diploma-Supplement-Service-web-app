/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uagean.dsIss.model.pojo.LinkedInUser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nikos
 */
public class LinkedInResponseParser {

    public static Map<String, String> parse(String linkedInResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, String> result = new HashMap();

        LinkedInUser user = mapper.readValue(linkedInResponse, LinkedInUser.class);
        result.put("eid",user.getId());
        result.put("firstName", user.getFirstName());
        result.put("lastName",user.getLastName());
        result.put("currentGivenName",user.getFirstName());
        result.put("currentFamilyName",user.getLastName());

        return result;
    }

}
