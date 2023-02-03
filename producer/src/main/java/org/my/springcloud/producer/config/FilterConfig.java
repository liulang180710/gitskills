package org.my.springcloud.producer.config;

import org.my.springcloud.producer.filter.HumanInfoFilter;
import org.my.springcloud.producer.filter.SessionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean sessionFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new SessionFilter());
        registrationBean.addUrlPatterns("/home/*");
        registrationBean.setName("sessionFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HumanInfoFilter> humanInfoFilterFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HumanInfoFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(-1);
        return registrationBean;
    }
}
