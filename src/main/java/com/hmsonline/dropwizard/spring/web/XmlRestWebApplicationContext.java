/**
 * Copyright (c) 2013 Health Market Science, Inc.
 */
package com.hmsonline.dropwizard.spring.web;

import com.hmsonline.dropwizard.spring.SpringConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.text.MessageFormat;

/**
 * XmlRestWebApplicationContext.
 */
public class XmlRestWebApplicationContext extends XmlWebApplicationContext {

    private boolean active;
    private String cfgLocationsType;

    public XmlRestWebApplicationContext(String[] configLocations, String cfgLocationsType, boolean refresh, ApplicationContext parent) {
        this.setConfigLocations(configLocations);
        this.setParent(parent);
        this.cfgLocationsType = cfgLocationsType;
        if (refresh) {
            this.refresh();
        }
    }

    @Override
    protected Resource getResourceByPath(String path) {
        if (getServletContext() == null) {
            // Load Resource based on cfgLocationsType.
            if (SpringConfiguration.FILE_CONFIG.equals(cfgLocationsType)) {
                return new FileSystemResource(path);
            } else if (SpringConfiguration.CLASSPATH_CONFIG.equals(cfgLocationsType)) {
                return new ClassPathResource(path);
            } else {
                throw new IllegalArgumentException(MessageFormat.format("Configuration Error: configLocationsType must be either \"{0}\" or \"{1}\"", SpringConfiguration.FILE_CONFIG, SpringConfiguration.CLASSPATH_CONFIG));
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