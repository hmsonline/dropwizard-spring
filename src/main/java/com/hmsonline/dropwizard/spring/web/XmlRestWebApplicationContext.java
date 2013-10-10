/**
 * Copyright (c) 2013 Health Market Science, Inc.
 */
package com.hmsonline.dropwizard.spring.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class XmlRestWebApplicationContext extends XmlWebApplicationContext {

    private boolean active;
    private String confLocationsType;

    public XmlRestWebApplicationContext(String[] configLocations, String confLocationsType, boolean refresh, ApplicationContext parent) {
        this.setConfigLocations(configLocations);
        this.setParent(parent);
        this.confLocationsType = confLocationsType;
        if (refresh) {
            this.refresh();
        }
    }

    @Override
    protected Resource getResourceByPath(String path) {
        if (getServletContext() == null) {
            // Load Resource based on confLocationsType
            if ("file".equals(confLocationsType)) {
                return new FileSystemResource(path);
            } else if ("classpath".equals(confLocationsType)) {
                return new ClassPathResource(path);
            } else {
                throw new IllegalArgumentException(
                        "Configuration Error: configLocationsType must be either 'file' or 'classpath'");
            }
        }
        return super.getResourceByPath(path);
    }

    @Override
    public synchronized void refresh() throws BeansException, IllegalStateException {
        if (!active) {
            super.refresh();
            active = true;
        }
    }
}