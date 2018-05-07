/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service.impl;

import gr.uagean.dsIss.model.pojo.Country;
import gr.uagean.dsIss.service.CountryService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author nikos
 */
@Service
public class CountryServiceImpl implements CountryService {

    private Logger log = LoggerFactory.getLogger(CountryService.class);

    @Override
    public List<Country> getEnabled() {

//       
        Properties prop = new Properties();
        InputStream input = null;
        HashMap<String, String> map = new HashMap();

        ArrayList<Country> countries = new ArrayList();

        try {
            input = new FileInputStream("/webappConfig/countries.properties");
            prop.load(input);
            prop.forEach((cntrName, code) -> {
                countries.add(new Country((String) cntrName, (String) code, true));
            });
        } catch (IOException ex) {
            log.info("ERROR reading countries", ex);
            return new ArrayList(
                    Arrays.asList(
                            new Country("iceland", "IS", true),
                            new Country("austria", "AU", true),
                            new Country("belgium", "BL", true),
                            new Country("bulgaria", "BG", true),
                            new Country("croatia", "CR", true),
                            new Country("cyprus", "CY", true),
                            new Country("czechrepublic", "CZ", true),
                            new Country("denmark", "DK", true),
                            new Country("estonia", "ES", true),
                            new Country("finland", "FN", true),
                            new Country("france", "FR", true),
                            new Country("germany", "AL", true),
                            new Country("greece", "GR", true),
                            new Country("hungary", "HN", true),
                            new Country("ireland", "IR", true),
                            new Country("italy", "IT", true),
                            new Country("latvia", "LV", true),
                            new Country("luxembourg", "LX", true),
                            new Country("malta", "ML", true),
                            new Country("netherlands", "NL", true),
                            new Country("poland", "POL", true),
                            new Country("portugal", "POR", true),
                            new Country("romania", "RO", true),
                            new Country("slovakia", "SL", true),
                            new Country("spain", "SP", true),
                            new Country("sweden", "SW", true),
                            new Country("test", "CA", true)
                    )
            );
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return countries;

    }

}
