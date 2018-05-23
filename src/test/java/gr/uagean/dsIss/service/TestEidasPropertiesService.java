/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service;

import gr.uagean.dsIss.service.EidasPropertiesService;
import gr.uagean.dsIss.service.impl.EidasPropertiesServiceImpl;
import gr.uagean.dsIss.service.impl.EidasPropertiesServiceImpl;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author nikos
 */

@RunWith(MockitoJUnitRunner.class)
public class TestEidasPropertiesService {

    private List<String> props;

    private EidasPropertiesService propServ;
    
    @Before
    public void before() {
        props = new ArrayList();
        props.add("http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName");
        props.add("http://eidas.europa.eu/attributes/legalperson/LegalName");
        propServ = Mockito.spy(new EidasPropertiesServiceImpl());
        Mockito.doReturn(props).when(propServ).getEidasProperties();
    }
    
    
    @Test
    public void testNaturalPerson(){
        assertEquals(propServ.getNaturalProperties().get(0),"Current Family Name");
        assertEquals(propServ.getLegalProperties().get(0),"Legal Name");
    }
}
