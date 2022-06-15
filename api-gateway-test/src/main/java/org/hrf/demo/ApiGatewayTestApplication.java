package org.hrf.demo;

import org.hrf.gateway.core.ApiContext;
import org.hrf.gateway.core.ApiServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayTestApplication {

    @Bean
    public ServletRegistrationBean apiServlet() {
        ServletRegistrationBean result = new ServletRegistrationBean(new ApiServlet());
        result.addUrlMappings("/api");
        return result;
    }

    static ApplicationContext context;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ApiGatewayTestApplication.class, args);
        ApiContext.init(context);
    }
}
