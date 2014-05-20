// Copyright (c) 2014 Health Market Science, Inc.

package com.hmsonline.dropwizard.spring.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author hieunguyen
 *
 */
public class ServletConfiguration {

    @JsonProperty
    private String clazz;

    @JsonProperty
    private String url;

    @JsonProperty
    private Map<String, String> param;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }
}
