/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.controllers;

import gr.uagean.dsIss.service.CountryService;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author nikos
 */
@Controller
public class ViewControllers {

    final static String ISS_URL = "ISS_URL";
    final static String SP_FAIL_PAGE = "SP_FAIL_PAGE";
    final static String SP_SUCCESS_PAGE = "SP_SUCCESS_PAGE";
    final static String SP_ID = "SP_ID";
    final static String SP_LOGO = "SP_LOGO";

    final static Logger log = LoggerFactory.getLogger(ViewControllers.class);

    @Autowired
    private CountryService countryServ;

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping("/login")
    public ModelAndView loginView(HttpServletRequest request) {
        UUID token = UUID.randomUUID();
        if (cacheManager.getCache("ips").get(request.getRemoteAddr()) != null) {
            cacheManager.getCache("ips").evict(request.getRemoteAddr());
        }
        System.getenv();
        cacheManager.getCache("ips").put(request.getRemoteAddr(), token);
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("issUrl", System.getenv(ISS_URL));
        mv.addObject("countries", countryServ.getEnabled());
        mv.addObject("token", token.toString());
        mv.addObject("sp", System.getenv(SP_ID));
        mv.addObject("logo", System.getenv(SP_LOGO));
        return mv;
    }

    @RequestMapping("/authsuccess")
    public String authorizationSuccess(@RequestParam(value = "t", required = true) String token,
            HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {

        if (cacheManager.getCache("ips").get(request.getRemoteAddr()) != null) {
            String jwt = cacheManager.getCache("tokens").get(token).get().toString();
            Cookie cookie = new Cookie("access_token", jwt);
            cookie.setPath("/");
            cookie.setMaxAge(30 * 60);
            response.addCookie(cookie);
            return "redirect:" + System.getenv(SP_SUCCESS_PAGE);
        }
        
        return  "redirect:" +System.getenv(SP_FAIL_PAGE);
    }
    
    
    
    @RequestMapping("/authfail")
    public String authorizationFail(@RequestParam(value = "t", required = true) String token,
            HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {

        return "redirect:" +System.getenv(SP_FAIL_PAGE);
    }

}
