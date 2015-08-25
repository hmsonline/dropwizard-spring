// Copyright (c) 2012 Health Market Science, Inc.

package com.hmsonline.dropwizard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path("/hello")
public class HelloResource {
    
    @Autowired
    private MessageBean messageBean;

    @Autowired
    private GreetingService greetingService;

    @GET
    @Path("/world")
    @Produces(MediaType.APPLICATION_JSON)
    public Object hello(){
        return greetingService.greet(messageBean.getMessage());
    }

}
