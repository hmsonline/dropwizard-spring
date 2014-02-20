package com.hmsonline.dropwizard.spring;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class ApiMetadata extends Configuration {

    @NotEmpty
    @JsonProperty
    private String rootServerName = "localhost";
    
    @NotEmpty
    @JsonProperty
    private String title = "";
    
    @NotEmpty
    @JsonProperty
    private String description = "";
    
    @NotEmpty
    @JsonProperty
    private String termsOfServiceUrl = "";
    
    @NotEmpty
    @JsonProperty
    private String contact = "";
    
    @NotEmpty
    @JsonProperty
    private String license = "";
    
    @NotEmpty
    @JsonProperty
    private String licenseUrl = "";

    public String getRootServerName() {
        return rootServerName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public String getContact() {
        return contact;
    }

    public String getLicense() {
        return license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }
    
}
