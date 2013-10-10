/**
 * Copyright (c) 2013 Health Market Science, Inc.
 */

package com.hmsonline.dropwizard.spring.web;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FilterConfiguration is config object for servlet or filter.
 */
public class FilterConfiguration {
    // Full name of filter/servlet class.
    @JsonProperty
    private String clazz;

    // Url pattern handled by this filter.
    @JsonProperty
    private String url;

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
}
