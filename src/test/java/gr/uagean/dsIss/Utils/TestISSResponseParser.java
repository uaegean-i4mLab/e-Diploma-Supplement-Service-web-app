/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.Utils;

import gr.uagean.dsIss.model.pojo.IssAttribute;
import gr.uagean.dsIss.utils.IssResponseParser;
import java.io.IOException;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author nikos
 */
public class TestISSResponseParser {
    
    @Test
    public void testIssResponse() throws IndexOutOfBoundsException, IOException{
        String responseString = "{\"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName\":{\"value\":\"javier\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\":{\"value\":\"Garcia\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth\":{\"value\":\"1965-01-01\",\"complex\":\"0\",\"required\":\"1\"},\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\":{\"value\":\"GR/GR/12345\",\"complex\":\"0\",\"required\":\"1\"}}";
        Map<String,String> parsedResponse = IssResponseParser.parse(responseString);
        assertEquals(parsedResponse.get("currentGivenName"),"javier");
        assertEquals(parsedResponse.get("personIdentifier"),"GR/GR/12345");
        assertEquals(parsedResponse.get("eid"),"GR/GR/12345");
    }
    
    
}
