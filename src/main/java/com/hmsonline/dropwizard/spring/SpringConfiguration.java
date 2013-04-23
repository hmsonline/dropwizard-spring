/*
Copyright 2013 Expedia

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

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
