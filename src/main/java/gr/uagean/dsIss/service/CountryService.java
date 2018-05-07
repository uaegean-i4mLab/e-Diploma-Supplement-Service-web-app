/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service;

import java.util.List;
import gr.uagean.dsIss.model.pojo.Country;

/**
 *
 * @author nikos
 */
public interface CountryService {
    
    public List<Country> getEnabled();
    
    
}
