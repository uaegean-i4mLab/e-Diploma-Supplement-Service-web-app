/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.controllers;

import gr.uagean.dsIss.model.pojo.LinkedInAuthAccessToken;
import gr.uagean.dsIss.service.CountryService;
import gr.uagean.dsIss.service.EidasPropertiesService;
import gr.uagean.dsIss.service.KeyStoreService;
import gr.uagean.dsIss.service.ParameterService;
import gr.uagean.dsIss.utils.CookieUtils;
import gr.uagean.dsIss.utils.JwtUtils;
import gr.uagean.dsIss.utils.LinkedInResponseParser;
import java.util.Map;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
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
    final static String UAEGEAN_LOGIN = "UAEGEAN_LOGIN";
    final static String LINKED_IN_SECRET = "LINKED_IN_SECRET";
    final static String SECRET = "SP_SECRET";

    final static String CLIENT_ID = "CLIENT_ID";
    final static String REDIRECT_URI = "REDIRECT_URI";
    final static String HTTP_HEADER = "HTTP_HEADER";
    final static String URL_ENCODED = "URL_ENCODED";

    final static Logger log = LoggerFactory.getLogger(ViewControllers.class);

    @Autowired
    private CountryService countryServ;

    @Autowired
    private EidasPropertiesService propServ;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ParameterService paramServ;

    @Autowired
    private KeyStoreService keyServ;

    @RequestMapping("/login")
    public ModelAndView loginView(HttpServletRequest request) {
        UUID token = UUID.randomUUID();
        if (cacheManager.getCache("ips").get(request.getRemoteAddr()) != null) {
            cacheManager.getCache("ips").evict(request.getRemoteAddr());
        }

        cacheManager.getCache("ips").put(request.getRemoteAddr(), token);
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("issUrl", paramServ.getParam(ISS_URL));
        mv.addObject("countries", countryServ.getEnabled());
        mv.addObject("token", token.toString());
        mv.addObject("sp", paramServ.getParam(SP_ID));
        mv.addObject("logo", paramServ.getParam(SP_LOGO));
        mv.addObject("spFailPage", paramServ.getParam(SP_FAIL_PAGE));

        mv.addObject("legal", propServ.getLegalProperties());
        mv.addObject("natural", propServ.getNaturalProperties());
        String uAegeanLogin = StringUtils.isEmpty(paramServ.getParam(UAEGEAN_LOGIN)) ? null : paramServ.getParam(UAEGEAN_LOGIN);
        mv.addObject("uAegeanLogin", uAegeanLogin);

        String clientID = paramServ.getParam(CLIENT_ID);
        String redirectURI = paramServ.getParam(REDIRECT_URI);
        String responseType = "code";
        String state = UUID.randomUUID().toString();
        mv.addObject("clientID", clientID);
        mv.addObject("redirectURI", redirectURI);
        mv.addObject("responseType", responseType);
        mv.addObject("state", state);
        boolean linkedIn = StringUtils.isEmpty(paramServ.getParam("LINKED_IN")) ? false : Boolean.parseBoolean(paramServ.getParam("LINKED_IN"));
        mv.addObject("linkedIn", linkedIn);

        return mv;
    }

    @RequestMapping("/authsuccess")
    public String authorizationSuccess(@RequestParam(value = "t", required = false) String token,
            HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {

        if (cacheManager.getCache("ips").get(request.getRemoteAddr()) != null) {
            String jwt = cacheManager.getCache("tokens").get(token).get().toString();

            if (paramServ.getParam(HTTP_HEADER) != null && Boolean.parseBoolean(paramServ.getParam(HTTP_HEADER))) {
                response.setHeader("Authorization", jwt);
            } else {

                if (paramServ.getParam(URL_ENCODED) != null && Boolean.parseBoolean(paramServ.getParam(URL_ENCODED))) {
                    return "redirect:" + paramServ.getParam(SP_SUCCESS_PAGE) + "?login=" + jwt;
                } else {
                    Cookie cookie = new Cookie("access_token", jwt);
                    cookie.setPath("/");
                    CookieUtils.addDurationIfNotNull(cookie, paramServ);
                    response.addCookie(cookie);

                }
            }
            return "redirect:" + paramServ.getParam(SP_SUCCESS_PAGE);
        }

        Cookie cookie = new Cookie("access_token", "");
        cookie.setPath("/");
        CookieUtils.addDurationIfNotNull(cookie, paramServ);
        response.addCookie(cookie);
        return "redirect:" + paramServ.getParam(SP_FAIL_PAGE);
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

        model.addAttribute("logo", paramServ.getParam(SP_LOGO));
        model.addAttribute("server", paramServ.getParam("SP_SERVER"));
        return "authfail";
    }

    @RequestMapping(value = "/linkedInResponse", method = {RequestMethod.POST, RequestMethod.GET})
    public String linkedInResponse(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String errorDescription,
            HttpServletResponse httpResponse) {

        //TODO Before you accept the authorization code, your application should ensure that the value returned in the state parameter matches the state value from your original authorization code request.
        if (org.apache.commons.lang3.StringUtils.isEmpty(error)) {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("grant_type", "authorization_code");
            map.add("code", code);
            map.add("redirect_uri", paramServ.getParam(REDIRECT_URI));
            map.add("client_id", paramServ.getParam(CLIENT_ID));
            map.add("client_secret", paramServ.getParam(LINKED_IN_SECRET));

            HttpEntity<MultiValueMap<String, String>> request
                    = new HttpEntity<>(map, headers);

            ResponseEntity<LinkedInAuthAccessToken> response = restTemplate
                    .exchange("https://www.linkedin.com/oauth/v2/accessToken", HttpMethod.POST, request, LinkedInAuthAccessToken.class);

            //TODO get User Data using accessToken 
            HttpHeaders headersUser = new HttpHeaders();
            headersUser.setContentType(MediaType.APPLICATION_JSON);
            headersUser.set("Authorization", "Bearer " + response.getBody().getAccess_token());
            HttpEntity<String> entity = new HttpEntity<String>("", headersUser);
            ResponseEntity<String> userResponse
                    = restTemplate.exchange("https://www.linkedin.com/v1/people/~:(id,firstName,lastName,email-address)?format=json",
                            HttpMethod.GET, entity, String.class); //user details https://www.linkedin.com/v1/people/~

            //return "token " + response.getBody().getAccess_token() + " , expires " + response.getBody().getExpires_in();
            //return userResponse.getBody();
            try {
                Map<String, String> jsonMap = LinkedInResponseParser.parse(userResponse.getBody());
                String access_token = JwtUtils.getJWT(jsonMap, paramServ, keyServ, "linkedIn");

                if (paramServ.getParam(HTTP_HEADER) != null && Boolean.parseBoolean(paramServ.getParam(HTTP_HEADER))) {
                    httpResponse.setHeader("Authorization", access_token);
                } else {
                    Cookie cookie = new Cookie("access_token", access_token);
                    cookie.setPath("/");
                    CookieUtils.addDurationIfNotNull(cookie, paramServ);
                    httpResponse.addCookie(cookie);
                }

                if (paramServ.getParam(HTTP_HEADER) != null && Boolean.parseBoolean(paramServ.getParam(HTTP_HEADER))) {
                    httpResponse.setHeader("Authorization", access_token);
                } else {

                    if (paramServ.getParam(URL_ENCODED) != null && Boolean.parseBoolean(paramServ.getParam(URL_ENCODED))) {
                        return "redirect:" + paramServ.getParam(SP_SUCCESS_PAGE) + "?login=" + access_token;
                    } else {
                        Cookie cookie = new Cookie("access_token", access_token);
                        cookie.setPath("/");
                        CookieUtils.addDurationIfNotNull(cookie, paramServ);
                        httpResponse.addCookie(cookie);

                    }
                }

                return "redirect:" + paramServ.getParam(SP_SUCCESS_PAGE);

            } catch (Exception e) {
                log.info("Exception", e);
            }

        }

        return "state" + state + " , code" + code;
    }

}
