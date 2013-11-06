// Copyright (c) 2012 Health Market Science, Inc.

package com.hmsonline.dropwizard.spring;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hmsonline.dropwizard.spring.web.FilterConfiguration;
import com.yammer.dropwizard.config.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public class SpringConfiguration extends Configuration {

    public static final String WEB_APPLICATION_CONTEXT = "web";
    public static final String APPLICATION_CONTEXT = "app";

    public static final String CLASSPATH_CONFIG = "classpath";
    public static final String FILE_CONFIG = "file";

    @NotEmpty
    @JsonProperty
    private String appContextType;

    @NotEmpty
    @JsonProperty
    private String configLocationsType;

    @NotEmpty
    @JsonProperty
    private List<String> configLocations;

    @NotEmpty
    @JsonProperty
    private List<String> resources;

    @JsonProperty
    private List<String> healthChecks;

    @JsonProperty
    private List<String> jerseyProviders;

    @JsonProperty
    private List<String> managed;

    @JsonProperty
    private List<String> lifeCycles;

    @JsonProperty
    private List<String> tasks;


    @JsonProperty
    private List<String> disabledJerseyFeatures;

    @JsonProperty
    private List<String> enabledJerseyFeatures;

    @JsonProperty
    private Map<String, FilterConfiguration> filters;


    public String getAppContextType() {
        return appContextType;
    }

    public List<String> getConfigLocations() {
        return configLocations;
    }

    public List<String> getResources() {
        return resources;
    }

    public List<String> getHealthChecks() {
        return healthChecks;
    }

    public List<String> getJerseyProviders() {
        return jerseyProviders;
    }

    public List<String> getManaged() {
        return managed;
    }

    public List<String> getLifeCycles() {
        return lifeCycles;
    }

    public List<String> getDisabledJerseyFeatures() {
        return disabledJerseyFeatures;
    }

    public List<String> getEnabledJerseyFeatures() {
        return enabledJerseyFeatures;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public Map<String, FilterConfiguration> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, FilterConfiguration> filters) {
        this.filters = filters;
    }

    public String getConfigLocationsType() {
        return configLocationsType;
    }

    public void setConfigLocationsType(String configLocationsType) {
        this.configLocationsType = configLocationsType;
    }
}
