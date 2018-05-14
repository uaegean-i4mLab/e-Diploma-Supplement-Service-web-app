/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.utils;

import javax.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author nikos
 */
public class CookieUtils {

    private static Logger log = LoggerFactory.getLogger(CookieUtils.class);

    public static void addDurationIfNotNull(Cookie cookie) {
        if (!StringUtils.isEmpty(System.getenv("AUTH_DURATION"))) {
            try {
                int maxAge = Integer.parseInt(System.getenv("AUTH_DURATION"));
                cookie.setMaxAge(maxAge);
            } catch (Exception e) {
                log.info("ERROR parsing AUTH_DURATION", e);
            }
        }

    }

}
