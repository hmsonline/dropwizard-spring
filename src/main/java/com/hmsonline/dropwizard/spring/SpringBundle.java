// Copyright (c) 2015 Hardiker Ltd.
package com.hmsonline.dropwizard.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.hmsonline.dropwizard.spring.web.XmlRestWebApplicationContext;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.springframework.context.ApplicationContext;

import java.util.List;


public class SpringBundle implements ConfiguredBundle<SpringServiceConfiguration> {

    SpringService service = new SpringService();

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // This is needed to avoid an exception when deserializing Json to an ArrayList<String>
        bootstrap.getObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    @Override
    public void run(SpringServiceConfiguration configuration, Environment environment) {
        SpringConfiguration config = configuration.getSpring();

        ApplicationContext parentCtx = initSpringParent();
        parentCtx = wrapApplicationContext(parentCtx, config);

        Dropwizard dw = (Dropwizard) parentCtx.getBean("dropwizard");
        dw.setConfiguration(configuration);
        dw.setEnvironment(environment);

        parentCtx = initSpringConfigBasedBeans(parentCtx, config);

        ApplicationContext appCtx = initSpring(config, parentCtx);
        loadResourceBeans(config.getResources(), appCtx, environment);
        loadHealthCheckBeans(config.getHealthChecks(), appCtx, environment);
        loadManagedBeans(config.getManaged(), appCtx, environment);
        loadLifeCycleBeans(config.getLifeCycles(), appCtx, environment);
        loadJerseyProviders(config.getJerseyProviders(), appCtx, environment);
        loadTasks(config.getTasks(), appCtx, environment);

        // Load filter or listeners for WebApplicationContext.
        if (appCtx instanceof XmlRestWebApplicationContext) {
            try {
                loadWebConfigs(environment, config, appCtx);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("CNFE when loading spring web configs: " + e.getMessage(), e);
            }
        }

        enableJerseyFeatures(config.getEnabledJerseyFeatures(), environment);
        disableJerseyFeatures(config.getDisabledJerseyFeatures(), environment);
    }

    /**
     * This allows you to wrap the ApplicationContext, potentially with another ApplicationContext to extend the base
     * functionality to meet your needs. Don't forget to refresh any new ApplicationContext's you create!
     *
     * @param parent Root Application Context
     * @param config SpringConfiguration object for reference
     * @return Application Context for further use
     */
    @SuppressWarnings("unused")
    protected ApplicationContext wrapApplicationContext(ApplicationContext parent, SpringConfiguration config) {
        return parent;
    }

    void loadWebConfigs(Environment environment, SpringConfiguration config, ApplicationContext appCtx) throws ClassNotFoundException {
        service.loadWebConfigs(environment, config, appCtx);
    }

    void loadResourceBeans(List<String> resources, ApplicationContext ctx, Environment env) {
        service.loadResourceBeans(resources, ctx, env);
    }

    void loadHealthCheckBeans(List<String> healthChecks, ApplicationContext ctx, Environment env) {
        service.loadHealthCheckBeans(healthChecks, ctx, env);
    }

    void loadManagedBeans(List<String> manageds, ApplicationContext ctx, Environment env) {
        service.loadManagedBeans(manageds, ctx, env);
    }

    void loadLifeCycleBeans(List<String> lifeCycles, ApplicationContext ctx, Environment env) {
        service.loadLifeCycleBeans(lifeCycles, ctx, env);
    }

    void loadJerseyProviders(List<String> providers, ApplicationContext ctx, Environment env) {
        service.loadJerseyProviders(providers, ctx, env);
    }

    void loadTasks(List<String> tasks, ApplicationContext ctx, Environment env) {
        service.loadTasks(tasks, ctx, env);
    }

    void enableJerseyFeatures(List<String> features, Environment env) {
        service.enableJerseyFeatures(features, env);
    }

    void disableJerseyFeatures(List<String> features, Environment env) {
        service.disableJerseyFeatures(features, env);
    }

    ApplicationContext initSpringParent() {
        return service.initSpringParent();
    }

    ApplicationContext initSpringConfigBasedBeans(ApplicationContext parent, SpringConfiguration springConfiguration) {
        return service.initSpringConfigBasedBeans(parent, springConfiguration);
    }

    ApplicationContext initSpring(SpringConfiguration config, ApplicationContext parent) {
        return service.initSpring(config, parent);
    }

}