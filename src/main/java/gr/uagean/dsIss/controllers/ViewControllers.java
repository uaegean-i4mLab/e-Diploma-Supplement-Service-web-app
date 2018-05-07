/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.controllers;

import gr.uagean.dsIss.service.CountryService;
import gr.uagean.dsIss.service.EidasPropertiesService;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

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
    final static String UAEGEAN_LOGIN="UAEGEAN_LOGIN";

    final static Logger log = LoggerFactory.getLogger(ViewControllers.class);

    @Autowired
    private CountryService countryServ;

    @Autowired
    private EidasPropertiesService propServ;

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
        mv.addObject("spFailPage", System.getenv(SP_FAIL_PAGE));

        mv.addObject("legal", propServ.getLegalProperties());
        mv.addObject("natural", propServ.getNaturalProperties());
        String uAegeanLogin  = StringUtils.isEmpty(System.getenv(UAEGEAN_LOGIN))?null:System.getenv(UAEGEAN_LOGIN);
        mv.addObject("uAegeanLogin", uAegeanLogin);
        return mv;
    }

    @RequestMapping("/authsuccess")
    public String authorizationSuccess(@RequestParam(value = "t", required = false) String token,
            HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {

        if (cacheManager.getCache("ips").get(request.getRemoteAddr()) != null) {
            String jwt = cacheManager.getCache("tokens").get(token).get().toString();
            Cookie cookie = new Cookie("access_token", jwt);
            cookie.setPath("/");
            int maxAge = Integer.parseInt(System.getenv("AUTH_DURATION"));
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);
            return "redirect:" + System.getenv(SP_SUCCESS_PAGE);
        }

        Cookie cookie = new Cookie("access_token", "");
        cookie.setPath("/");
        int maxAge = Integer.parseInt(System.getenv("AUTH_DURATION"));
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
        return "redirect:" + System.getenv(SP_FAIL_PAGE);
    }

    @RequestMapping("/authfail")
    public String authorizationFail(@RequestParam(value = "t", required = false) String token,
            @RequestParam(value = "reason", required = false) String reason,
            Model model) {

        Cache.ValueWrapper errorMsg = token != null ? cacheManager.getCache("errors").get(token) : null;
        if (token != null && model.asMap().get("errorMsg") == null) {
            if (errorMsg != null && errorMsg.get() != null) {
                model.addAttribute("title", "Registration/Login Cancelled");
                model.addAttribute("errorMsg", errorMsg.get());
            } else {
                model.addAttribute("title", "Non-sucessful authentication");
                model.addAttribute("errorMsg", "Please, return to the home page and re-initialize the process. If the authentication fails again, please contact your national eID provider");
            }
        }

        if (reason != null) {
            if (reason.equals("disagree")) {
                model.addAttribute("errorType", "DISAGREE");
            } else {
                model.addAttribute("errorType", "CANCEL");
            }
            model.addAttribute("title", "");
            model.addAttribute("errorMsg", "Registration/Login Cancelled");
        }

        model.addAttribute("logo", System.getenv(SP_LOGO));
        model.addAttribute("server", System.getenv("SP_SERVER"));
        return "authfail";
    }

}
