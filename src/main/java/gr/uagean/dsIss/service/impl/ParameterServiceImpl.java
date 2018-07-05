/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.service.impl;

import gr.uagean.dsIss.service.ParameterService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 *
 * @author nikos
 */
@Service
public class ParameterServiceImpl implements ParameterService {

    private final Logger log = LoggerFactory.getLogger(ParameterServiceImpl.class);

    private final Map<String, String> properties;

    public ParameterServiceImpl() {
        properties = getConfigProperties();
    }

    @Override
    public String getParam(String paramName) {
        if (StringUtils.isEmpty(System.getenv(paramName))) {
            return properties.get(paramName);
        }
        return System.getenv(paramName);
    }

    private Map<String, String> getConfigProperties() {
        Properties prop = new Properties();
        InputStream input = null;
        HashMap<String, String> map = new HashMap();
        try {
            input = new FileInputStream("/webappConfig/config.properties");
            prop.load(input);
            prop.forEach((key, value) -> {
                map.put((String) key, (String) value);
            });
        } catch (IOException ex) {
            log.info("Properties file not found in /webappConfig/config.properties", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

}
