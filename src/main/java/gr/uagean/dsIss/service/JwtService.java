/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service;

import java.util.Map;

/**
 *
 * @author nikos
 */
public interface JwtService {
    
    public String getJWT(Map<String, String> jsonMap,ParameterService paramServ);


}
