// Copyright (c) 2015 Hardiker Ltd.
package com.hmsonline.dropwizard.spring;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class BeanConfiguration {

    @JsonProperty
    private String clazz; // Filter class.

    @JsonProperty
    private Map<String, Object> config; // Init params.

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
