/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uagean.dsIss.model.pojo.IssErrorResponse;
import java.io.IOException;

/**
 *
 * @author nikos
 */
public class IssErrorMapper {


    public static IssErrorResponse wrapErrorToObject(String errorJson) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(errorJson, IssErrorResponse.class);
    }

    
}