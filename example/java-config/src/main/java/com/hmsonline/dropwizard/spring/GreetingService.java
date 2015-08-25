package com.hmsonline.dropwizard.spring;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String greet(String name) {
        return "Hi " + name;
    }

}
