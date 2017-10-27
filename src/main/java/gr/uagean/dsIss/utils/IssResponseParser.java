/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import gr.uagean.dsIss.model.pojo.IssAttribute;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author nikos
 */
public class IssResponseParser {

    public static Map<String, String> parse(String issResponse) throws IndexOutOfBoundsException, IOException {

        Map<String, String> res =  parseISSResponse(issResponse).entrySet().stream().collect(Collectors.toMap(
                e -> {
                    String name = e.getKey().replace("http://eidas.europa.eu/attributes/naturalperson/representative/", "")
                            .replace("http://eidas.europa.eu/attributes/legalperson/representative/", "")
                            .replace("http://eidas.europa.eu/attributes/naturalperson/", "")
                            .replace("http://eidas.europa.eu/attributes/legalperson/", "");
                    char c[] = name.toCharArray();
                    c[0] = Character.toLowerCase(c[0]);
                    name = new String(c);
                    return name;
                },
                e -> {
                    return e.getValue().getValue();
                }));
        res.put("eid",res.get("personIdentifier"));
        return res;

    }

    private static Map<String, IssAttribute> parseISSResponse(String jsonString) throws IOException {
        ObjectMapper jmap = new ObjectMapper();
        TypeFactory typeFactory = jmap.getTypeFactory();
        MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, IssAttribute.class);
        return jmap.readValue(jsonString, mapType);
    }

}
