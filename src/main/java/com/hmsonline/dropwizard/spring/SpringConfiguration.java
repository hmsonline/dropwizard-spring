// Copyright (c) 2012 Health Market Science, Inc.

package com.hmsonline.dropwizard.spring;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hmsonline.dropwizard.spring.web.FilterConfiguration;
import com.hmsonline.dropwizard.spring.web.ServletConfiguration;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringConfiguration extends Configuration {

    public enum ApplicationContextType {
        WEB_APPLICATION_CONTEXT("web"),APPLICATION_CONTEXT("app");

        private String name;

        ApplicationContextType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }



    public static final String CLASSPATH_CONFIG = "classpath";
    public static final String FILE_CONFIG = "file";

    @NotEmpty
    @JsonProperty
    @JsonDeserialize(using = ApplicationContextTypeDeserializer.class)
    private ApplicationContextType appContextType;

    @NotEmpty
    @JsonProperty
    private String configLocationsType;

    @JsonProperty
    private List<String> configLocations;

    @JsonProperty
    private List<String> javaConfigBasePackages;

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

    @JsonProperty
    private Map<String, ServletConfiguration> servlets;

    public ApplicationContextType getAppContextType() {
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

    public Map<String, ServletConfiguration> getServlets() {
        return servlets;
    }

    public void setServlets(Map<String, ServletConfiguration> servlets) {
        this.servlets = servlets;
    }

    public String getConfigLocationsType() {
        return configLocationsType;
    }

    public void setConfigLocationsType(String configLocationsType) {
        this.configLocationsType = configLocationsType;
    }

    public List<String> getJavaConfigBasePackages() {
        return javaConfigBasePackages;
    }

    public void setJavaConfigBasePackages(List<String> javaConfigBasePackages) {
        this.javaConfigBasePackages = javaConfigBasePackages;
    }
}
