/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.utils;

import gr.uagean.dsIss.model.pojo.IssAttribute;
import gr.uagean.dsIss.model.pojo.IssAttributeList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author nikos
 */
public class Wrappers {

    public static IssAttributeList wrapEidasPropsToIssAttrs(List<String> eidasProperties) {
        IssAttributeList issAttrList = new IssAttributeList();
        final HashMap<String, IssAttribute> props = new HashMap();
        eidasProperties.stream().forEach(property -> {
            props.put(property, new IssAttribute(null, 0, 1));
        });
        issAttrList.setList(props);
        return issAttrList;
    }

}
