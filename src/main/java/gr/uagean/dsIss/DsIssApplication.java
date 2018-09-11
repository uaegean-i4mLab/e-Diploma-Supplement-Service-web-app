package gr.uagean.dsIss;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@SpringBootApplication
@EnableCaching
public class DsIssApplication {

    public static void main(String[] args) {
        SpringApplication.run(DsIssApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver r = new CookieLocaleResolver();
        r.setDefaultLocale(Locale.US);
        r.setCookieName("localeInfo");
        //if set to -1, the cookie is deleted
        // when browser shuts down
        r.setCookieMaxAge(24 * 60 * 60);
        return r;
    }

    @Bean
    public WebMvcConfigurer configurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                LocaleChangeInterceptor l = new LocaleChangeInterceptor();
                l.setParamName("localeCode");
                registry.addInterceptor(l);
            }
        };
    }
}
