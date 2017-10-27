/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service;

import java.io.IOException;
import java.util.List;
import org.apache.http.NameValuePair;
//import org.apache.commons.httpclient.NameValuePair;

/**
 *
 * @author nikos
 */
public interface NetworkService {
    
    public boolean sendPostReqWithData(String url, List<NameValuePair> urlParameters) throws IOException ;
    
}