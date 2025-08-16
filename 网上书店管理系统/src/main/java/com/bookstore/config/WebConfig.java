package com.bookstore.config;


import com.bookstore.Filter.LoginCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<LoginCheckFilter> loginCheckFilter() {
        FilterRegistrationBean<LoginCheckFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginCheckFilter());
        registrationBean.addUrlPatterns("/*"); // 拦截所有路径
        return registrationBean;
    }
}
