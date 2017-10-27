/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service.impl;

import gr.uagean.dsIss.model.pojo.Country;
import gr.uagean.dsIss.service.CountryService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author nikos
 */
@Service
public class CountryServiceImpl implements CountryService {

    @Override
    public List<Country> getEnabled() {

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
    }

}
