// Copyright (c) 2015 Hardiker Ltd.
package com.hmsonline.dropwizard.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.hmsonline.dropwizard.spring.web.XmlRestWebApplicationContext;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.springframework.context.ApplicationContext;


public class SpringBundle implements ConfiguredBundle<SpringServiceConfiguration> {

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // This is needed to avoid an exception when deserializing Json to an ArrayList<String>
        bootstrap.getObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    @Override
    public void run(SpringServiceConfiguration configuration, Environment environment) {
        SpringConfiguration config = configuration.getSpring();

        SpringService service = new SpringService();
        ApplicationContext parentCtx = service.initSpringParent();

        Dropwizard dw = (Dropwizard) parentCtx.getBean("dropwizard");
        dw.setConfiguration(configuration);
        dw.setEnvironment(environment);

        ApplicationContext appCtx = service.initSpring(config, parentCtx);
        service.loadResourceBeans(config.getResources(), appCtx, environment);
        service.loadHealthCheckBeans(config.getHealthChecks(), appCtx, environment);
        service.loadManagedBeans(config.getManaged(), appCtx, environment);
        service.loadLifeCycleBeans(config.getLifeCycles(), appCtx, environment);
        service.loadJerseyProviders(config.getJerseyProviders(), appCtx, environment);
        service.loadTasks(config.getTasks(), appCtx, environment);

        // Load filter or listeners for WebApplicationContext.
        if (appCtx instanceof XmlRestWebApplicationContext) {
            try {
                service.loadWebConfigs(environment, config, appCtx);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("CNFE when loading spring web configs: " + e.getMessage(), e);
            }
        }

        service.enableJerseyFeatures(config.getEnabledJerseyFeatures(), environment);
        service.disableJerseyFeatures(config.getDisabledJerseyFeatures(), environment);
    }

}