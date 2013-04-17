// Copyright (c) 2012 Health Market Science, Inc.

package com.hmsonline.dropwizard.spring;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class SpringServiceConfiguration extends Configuration {
    
    @NotNull
    @JsonProperty
    private SpringConfiguration spring;
    
    public SpringConfiguration getSpring(){
        return this.spring;
    }

}
