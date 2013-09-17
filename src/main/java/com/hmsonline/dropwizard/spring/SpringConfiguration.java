package com.hmsonline.dropwizard.spring;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class SpringConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String appContextType;
    
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
    
    

}
