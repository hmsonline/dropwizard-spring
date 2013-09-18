// Copyright (c) 2012 Health Market Science, Inc.

package com.hmsonline.dropwizard.spring;

import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

public class Dropwizard {

    private Configuration configuration;

    private Environment environment;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
