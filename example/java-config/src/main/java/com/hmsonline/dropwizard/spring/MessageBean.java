package com.hmsonline.dropwizard.spring;

import org.springframework.stereotype.Component;

public class MessageBean {

    private String message;

    public MessageBean(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
