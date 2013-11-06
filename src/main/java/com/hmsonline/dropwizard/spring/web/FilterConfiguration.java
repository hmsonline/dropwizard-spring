/**
 * Copyright (c) 2013 Health Market Science, Inc.
 */

package com.hmsonline.dropwizard.spring.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class FilterConfiguration {

    @JsonProperty
    private String clazz; // Filter class.

    @JsonProperty
    private String url; // URL pattern

    @JsonProperty
    private Map<String, String> param; // Init params.

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }
}
