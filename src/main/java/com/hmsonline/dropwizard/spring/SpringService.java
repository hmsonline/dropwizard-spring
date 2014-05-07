// Copyright (c) 2012 Health Market Science, Inc.
package com.hmsonline.dropwizard.spring;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.codahale.metrics.health.HealthCheck;
import com.hmsonline.dropwizard.spring.web.FilterConfiguration;
import com.hmsonline.dropwizard.spring.web.RestContextLoaderListener;
import com.hmsonline.dropwizard.spring.web.XmlRestWebApplicationContext;
import io.dropwizard.Application;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlet.FilterHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.fasterxml.jackson.databind.DeserializationFeature;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

public class SpringService extends Application<SpringServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new SpringService().run(args);
    }

    @Override
    public String getName() {
        return "dropwizard-spring";
    }

    @Override
    public void initialize(Bootstrap<SpringServiceConfiguration> bootstrap) {
        // This is needed to avoid an exception when deserializing Json to an
        // ArrayList<String>
        bootstrap.getObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    @Override
    public void run(SpringServiceConfiguration configuration, Environment environment) throws ClassNotFoundException {
        SpringConfiguration config = configuration.getSpring();

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

    }

    /**
     * Load filter or listeners for WebApplicationContext.
     */
    private void loadWebConfigs(Environment environment, SpringConfiguration config, ApplicationContext appCtx) throws ClassNotFoundException {
        // Load filters.
        loadFilters(config.getFilters(), appCtx, environment);

        // Load servlet listener.
        environment.servlets().addServletListeners(new RestContextLoaderListener((XmlRestWebApplicationContext) appCtx));
    }

    /**
     * Load all filters.
     */
    private void loadFilters(Map<String, FilterConfiguration> filters, ApplicationContext appCtx, Environment environment) throws ClassNotFoundException {
        if (filters != null) {
            for (Map.Entry<String, FilterConfiguration> filterEntry : filters.entrySet()) {
                FilterConfiguration filter = filterEntry.getValue();
                // Add filter
                FilterHolder filterHolder = environment.getApplicationContext().addFilter((Class<? extends Filter>) Class.forName(filter.getClazz()), filter.getUrl(), EnumSet.of(DispatcherType.REQUEST));

                // Set name of filter
                filterHolder.setName(filterEntry.getKey());
                // Set params
                if (filter.getParam() != null) {
                    for (Map.Entry<String, String> entry : filter.getParam().entrySet()) {
                        filterHolder.setInitParameter(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    private void loadResourceBeans(List<String> resources, ApplicationContext ctx, Environment env) {
        if (resources != null) {
            for (String resource : resources) {
                env.jersey().register(ctx.getBean(resource));
            }
        }

    }

    private void loadHealthCheckBeans(List<String> healthChecks, ApplicationContext ctx, Environment env) {
        if (healthChecks != null) {
            for (String healthCheck : healthChecks) {
                HealthCheck healthCheckBean = (HealthCheck) ctx.getBean(healthCheck);
                env.healthChecks().register(healthCheckBean.getClass().getSimpleName(), healthCheckBean);
            }
        }
    }

    private void loadManagedBeans(List<String> manageds, ApplicationContext ctx, Environment env) {
        if (manageds != null) {
            for (String managed : manageds) {
                env.getApplicationContext().manage(ctx.getBean(managed));
            }
        }
    }

    private void loadLifeCycleBeans(List<String> lifeCycles, ApplicationContext ctx, Environment env) {
        if (lifeCycles != null) {
            for (String lifeCycle : lifeCycles) {
                env.getApplicationContext().manage(ctx.getBean(lifeCycle));
            }
        }
    }

    private void loadJerseyProviders(List<String> providers, ApplicationContext ctx, Environment env) {
        if (providers != null) {
            for (String provider : providers) {
                env.jersey().register(ctx.getBean(provider));
            }
        }
    }

    private void loadTasks(List<String> tasks, ApplicationContext ctx, Environment env) {
        if (tasks != null) {
            for (String task : tasks) {
                env.admin().addTask((Task) ctx.getBean(task));
            }
        }
    }

    private void enableJerseyFeatures(List<String> features, Environment env) {
        if (features != null) {
            for (String feature : features) {
                env.jersey().enable(feature);
            }
        }
    }

    private void disableJerseyFeatures(List<String> features, Environment env) {
        if (features != null) {
            for (String feature : features) {
                env.jersey().disable(feature);
            }
        }
    }

    private ApplicationContext initSpringParent() {
        ApplicationContext parent = new ClassPathXmlApplicationContext(
                new String[]{"dropwizardSpringApplicationContext.xml"}, true);
        return parent;
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

}
