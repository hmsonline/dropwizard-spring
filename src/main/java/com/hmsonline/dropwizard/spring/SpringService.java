// Copyright (c) 2012 Health Market Science, Inc.
package com.hmsonline.dropwizard.spring;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.hmsonline.dropwizard.spring.web.FilterConfiguration;
import com.hmsonline.dropwizard.spring.web.RestContextLoaderListener;
import com.hmsonline.dropwizard.spring.web.ServletConfiguration;
import com.hmsonline.dropwizard.spring.web.XmlRestWebApplicationContext;
import com.yammer.dropwizard.config.FilterBuilder;
import com.yammer.dropwizard.config.ServletBuilder;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.dropwizard.tasks.Task;
import com.yammer.metrics.core.HealthCheck;

import javax.servlet.Filter;
import javax.servlet.Servlet;

public class SpringService extends Service<SpringServiceConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(SpringService.class);

    public static void main(String[] args) throws Exception {
        new SpringService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SpringServiceConfiguration> bootstrap) {
        bootstrap.setName("dropwizard-spring");
        // This is needed to avoid an exception when deserializing Json to an
        // ArrayList<String>
        bootstrap.getObjectMapperFactory().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    @Override
    public void run(SpringServiceConfiguration configuration, Environment environment) throws ClassNotFoundException {
        SpringConfiguration config = configuration.getSpring();

        setProfiles(config.getProfiles());
        
        ApplicationContext parentCtx = this.initSpringParent();

        Dropwizard dw = (Dropwizard) parentCtx.getBean("dropwizard");
        dw.setConfiguration(configuration);
        dw.setEnvironment(environment);

        ApplicationContext appCtx = initSpring(config, parentCtx);
        loadResourceBeans(config.getResources(), appCtx, environment);
        loadHealthCheckBeans(config.getHealthChecks(), appCtx, environment);
        loadManagedBeans(config.getManaged(), appCtx, environment);
        loadLifeCycleBeans(config.getLifeCycles(), appCtx, environment);
        loadJerseyProviders(config.getJerseyProviders(), appCtx, environment);
        loadTasks(config.getTasks(), appCtx, environment);

        // Load filter or listeners for WebApplicationContext.
        if (appCtx instanceof XmlRestWebApplicationContext) {
            loadWebConfigs(environment, config, appCtx);
        }

        enableJerseyFeatures(config.getEnabledJerseyFeatures(), environment);
        disableJerseyFeatures(config.getDisabledJerseyFeatures(), environment);

        setJerseyProperties(config.getJerseyProperties(), environment);
        
    }

    /**
     * Load filter or listeners for WebApplicationContext.
     */
    private void loadWebConfigs(Environment environment, SpringConfiguration config, ApplicationContext appCtx) throws ClassNotFoundException {
        // Load filters.
        loadFilters(config.getFilters(), appCtx, environment);

        // Load servlet listener.
        environment.addServletListeners(new RestContextLoaderListener((XmlRestWebApplicationContext) appCtx));

        // Load servlets.
        loadServlets(config.getServlets(), appCtx, environment);
    }

    /**
     * Load all filters.
     */
    private void loadFilters(Map<String, FilterConfiguration> filters, ApplicationContext appCtx, Environment environment) throws ClassNotFoundException {
        if (filters != null) {
            for (Map.Entry<String, FilterConfiguration> filterEntry : filters.entrySet()) {
                FilterConfiguration filter = filterEntry.getValue();
                // Add filter
                FilterBuilder filterConfig = environment.addFilter((Class<? extends Filter>) Class.forName(filter.getClazz()), filter.getUrl());
                // Set name of filter
                filterConfig.setName(filterEntry.getKey());
                // Set params
                if (filter.getParam() != null) {
                    for (Map.Entry<String, String> entry : filter.getParam().entrySet()) {
                        filterConfig.setInitParam(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    /**
     * Load all servlets.
     */
    private void loadServlets(Map<String, ServletConfiguration> servlets, ApplicationContext appCtx,
            Environment environment) throws ClassNotFoundException {
        if (servlets != null) {
            for (Map.Entry<String, ServletConfiguration> servletEntry : servlets.entrySet()) {
                ServletConfiguration servlet = servletEntry.getValue();
                // Add servlet
                ServletBuilder servletBuilder = environment.addServlet(
                        (Class<? extends Servlet>) Class.forName(servlet.getClazz()), servlet.getUrl());
                // Set name of servlet
                servletBuilder.setName(servletEntry.getKey());

                // Set params
                if (servlet.getParam() != null) {
                    for (Map.Entry<String, String> entry : servlet.getParam().entrySet()) {
                        servletBuilder.setInitParam(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    private void loadResourceBeans(List<String> resources, ApplicationContext ctx, Environment env) {
        if (resources != null) {
            for (String resource : resources) {
                try {
                    env.addResource(ctx.getBean(resource));
                } catch (NoSuchBeanDefinitionException nsbde) {
                    logNoSuchBeanDefinitionException(nsbde);
                }
            }
        }

    }

    private void loadHealthCheckBeans(List<String> healthChecks, ApplicationContext ctx, Environment env) {
        if (healthChecks != null) {
            for (String healthCheck : healthChecks) {
                try {
                    env.addHealthCheck((HealthCheck) ctx.getBean(healthCheck));
                } catch (NoSuchBeanDefinitionException nsbde) {
                    logNoSuchBeanDefinitionException(nsbde);
                }
            }
        }
    }

    private void loadManagedBeans(List<String> manageds, ApplicationContext ctx, Environment env) {
        if (manageds != null) {
            for (String managed : manageds) {
                try {
                    env.manage((Managed) ctx.getBean(managed));
                } catch (NoSuchBeanDefinitionException nsbde) {
                    logNoSuchBeanDefinitionException(nsbde);
                }
            }
        }
    }

    private void loadLifeCycleBeans(List<String> lifeCycles, ApplicationContext ctx, Environment env) {
        if (lifeCycles != null) {
            for (String lifeCycle : lifeCycles) {
                try {
                    env.manage((LifeCycle) ctx.getBean(lifeCycle));
                } catch (NoSuchBeanDefinitionException nsbde) {
                    logNoSuchBeanDefinitionException(nsbde);
                }
            }
        }
    }

    private void loadJerseyProviders(List<String> providers, ApplicationContext ctx, Environment env) {
        if (providers != null) {
            for (String provider : providers) {
                try {
                    env.addProvider(ctx.getBean(provider));
                } catch (NoSuchBeanDefinitionException nsbde) {
                    logNoSuchBeanDefinitionException(nsbde);
                }
            }
        }
    }

    private void loadTasks(List<String> tasks, ApplicationContext ctx, Environment env) {
        if (tasks != null) {
            for (String task : tasks) {
                try {
                    env.addTask((Task) ctx.getBean(task));
                } catch (NoSuchBeanDefinitionException nsbde) {
                    logNoSuchBeanDefinitionException(nsbde);
                }
            }
        }
    }

    private void enableJerseyFeatures(List<String> features, Environment env) {
        if (features != null) {
            for (String feature : features) {
                env.enableJerseyFeature(feature);
            }
        }
    }

    private void disableJerseyFeatures(List<String> features, Environment env) {
        if (features != null) {
            for (String feature : features) {
                env.disableJerseyFeature(feature);
            }
        }
    }
    
    private void setJerseyProperties(Map<String, String> properties, Environment env) {
        if (properties != null) {
            for (Map.Entry<String, String> propertyEntry : properties.entrySet()) {
                env.setJerseyProperty(propertyEntry.getKey(), propertyEntry.getValue());
            }
        }
    }

    private ApplicationContext initSpringParent() {
        ApplicationContext parent = new ClassPathXmlApplicationContext(
                new String[]{"dropwizardSpringApplicationContext.xml"}, true);
        return parent;
    }
    
    private void setProfiles(List<String> profiles) {
        if (CollectionUtils.isEmpty(profiles)) {
            return;
        }
        
        System.setProperty("spring.profiles.active", StringUtils.join(profiles, ","));
    }

    private ApplicationContext initSpring(SpringConfiguration config, ApplicationContext parent) {
        ApplicationContext appCtx = null;
        // Get Application Context Type
        String ctxType = config.getAppContextType();
        // Get Config Location Type.
        String cfgLocationType = config.getConfigLocationsType();
        String[] configLocations = config.getConfigLocations().toArray(new String[config.getConfigLocations().size()]);

        if (SpringConfiguration.WEB_APPLICATION_CONTEXT.equals(ctxType)) {
            // Create Web Application Context.
            appCtx = new XmlRestWebApplicationContext(configLocations, cfgLocationType, true, parent);

        } else if (SpringConfiguration.APPLICATION_CONTEXT.equals(ctxType)) {

            // Create Application Context.
            if (SpringConfiguration.FILE_CONFIG.equals(cfgLocationType)) {
                appCtx = new FileSystemXmlApplicationContext(configLocations, true, parent);
            } else if (SpringConfiguration.CLASSPATH_CONFIG.equals(cfgLocationType)) {
                appCtx = new ClassPathXmlApplicationContext(configLocations, true, parent);
            } else {
                throw new IllegalArgumentException(MessageFormat.format("Configuration Error: configLocationsType must be either \"{0}\" or \"{1}\"", SpringConfiguration.FILE_CONFIG, SpringConfiguration.CLASSPATH_CONFIG));
            }
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Configuration Error: appContextType must be either \"{0}\" or \"{1}\"", SpringConfiguration.WEB_APPLICATION_CONTEXT, SpringConfiguration.APPLICATION_CONTEXT));
        }
        return appCtx;
    }

    private void logNoSuchBeanDefinitionException(NoSuchBeanDefinitionException nsbde) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("Skipping missing Spring bean: ", nsbde);
        }
    }
}
