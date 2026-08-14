package az.ingress.hrms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    //allows your HRMS application to make HTTP requests to another application.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}