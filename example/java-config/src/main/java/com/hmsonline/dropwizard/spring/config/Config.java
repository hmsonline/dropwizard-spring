package com.hmsonline.dropwizard.spring.config;

import com.hmsonline.dropwizard.spring.MessageBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.hmsonline.dropwizard.spring")
public class Config {

    @Bean
    MessageBean createMessageBean() {
        return new MessageBean("Java Config");
    }

}
