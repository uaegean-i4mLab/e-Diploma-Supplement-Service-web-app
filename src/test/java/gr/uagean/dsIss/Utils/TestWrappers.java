/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gr.uagean.dsIss.model.pojo.IssAttributeList;
import gr.uagean.dsIss.utils.Wrappers;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author nikos
 */
public class TestWrappers {

    private List<String> eidasProps = new ArrayList();

    @Before
    public void before() {
        eidasProps.add("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier");
        eidasProps.add("http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName");
    }

    

    @Test
    public void testWrapEidasPropsToIssAttrs() throws JsonProcessingException {
        IssAttributeList result = Wrappers.wrapEidasPropsToIssAttrs(eidasProps);
        assertEquals(result.getStatus(), "OK");
        assertEquals(result.getList().size(), 2);
        assertNotNull(result.getList().get("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier"));
        assertNotNull(result.getList().get("http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName"));
        assertEquals(result.getList().get("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier").getValue(), null);
        assertEquals(result.getList().get("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier").getComplex(), 0);
        assertEquals(result.getList().get("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier").getRequired(), 1);
    }

    @Test
    public void testWrapEidasPropsToIssAttrsJSONStrings() throws JsonProcessingException {
        IssAttributeList result = Wrappers.wrapEidasPropsToIssAttrs(eidasProps);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(result);
        String expected = "{\n"
                + "   \"status\":\"OK\",\n"
                + "   \"list\":{\n"
                + "\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\":{\n"
                + "  \"value\":null,\n"
                + "         \"complex\":0 ,\n"
                + "         \"required\":1\n"
                + "},\n"
                + "      \"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\":{\n"
                + "  \"value\":null,\n"
                + "         \"complex\":0 ,\n"
                + "         \"required\":1\n"
                + "}\n"
                + "}\n"
                + "}";

        assertEquals(json.replaceAll("\\s+", ""), expected.replaceAll("\\s+", ""));
    }
}
