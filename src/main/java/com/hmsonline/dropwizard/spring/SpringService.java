// Copyright (c) 2012 Health Market Science, Inc.
package com.hmsonline.dropwizard.spring;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.hmsonline.dropwizard.spring.web.FilterConfiguration;
import com.hmsonline.dropwizard.spring.web.RestContextLoaderListener;
import com.hmsonline.dropwizard.spring.web.ServletConfiguration;
import com.hmsonline.dropwizard.spring.web.XmlRestWebApplicationContext;
import io.dropwizard.Application;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;

import static com.hmsonline.dropwizard.spring.SpringConfiguration.ApplicationContextType.WEB_APPLICATION_CONTEXT;


public class SpringService extends Application<SpringServiceConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(SpringService.class);

    public static void main(String[] args) throws Exception {
        new SpringService().run(args);
    }

    @Override
    public String getName() {
        return "dropwizard-spring";
    }

    @Override
    public void initialize(Bootstrap<SpringServiceConfiguration> bootstrap) {
        // This is needed to avoid an exception when deserializing Json to an ArrayList<String>
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
     * Load filter, servlets or listeners for WebApplicationContext.
     */
    private void loadWebConfigs(Environment environment, SpringConfiguration config, ApplicationContext appCtx) throws ClassNotFoundException {
        // Load filters.
        loadFilters(config.getFilters(), environment);

        // Load servlet listener.
        environment.servlets().addServletListeners(new RestContextLoaderListener((XmlRestWebApplicationContext) appCtx));

        // Load servlets.
        loadServlets(config.getServlets(), environment);
    }

    /**
     * Load all filters.
     */
    @SuppressWarnings("unchecked")
    private void loadFilters(Map<String, FilterConfiguration> filters, Environment environment) throws ClassNotFoundException {
        if (filters != null) {
            for (Map.Entry<String, FilterConfiguration> filterEntry : filters.entrySet()) {
                FilterConfiguration filter = filterEntry.getValue();

                // Create filter holder
                FilterHolder filterHolder = new FilterHolder((Class<? extends Filter>) Class.forName(filter.getClazz()));

                // Set name of filter
                filterHolder.setName(filterEntry.getKey());
                
                // Set params
                if (filter.getParam() != null) {
                    for (Map.Entry<String, String> entry : filter.getParam().entrySet()) {
                        filterHolder.setInitParameter(entry.getKey(), entry.getValue());
                    }
                }

                // Add filter
                environment.getApplicationContext().addFilter(filterHolder, filter.getUrl(), EnumSet.of(DispatcherType.REQUEST));
            }
        }
    }

    /**
     * Load all servlets.
     */
    @SuppressWarnings("unchecked")
    private void loadServlets(Map<String, ServletConfiguration> servlets, Environment environment) throws ClassNotFoundException {
        if (servlets != null) {
            for (Map.Entry<String, ServletConfiguration> servletEntry : servlets.entrySet()) {
                ServletConfiguration servlet = servletEntry.getValue();

                // Create servlet holder
                ServletHolder servletHolder = new ServletHolder((Class<? extends Servlet>) Class.forName(servlet.getClazz()));

                // Set name of servlet
                servletHolder.setName(servletEntry.getKey());

                // Set params
                if (servlet.getParam() != null) {
                    for (Map.Entry<String, String> entry : servlet.getParam().entrySet()) {
                        servletHolder.setInitParameter(entry.getKey(), entry.getValue());
                    }
                }

                // Add servlet
                environment.getApplicationContext().addServlet(servletHolder, servlet.getUrl());
            }
        }
    }

    private void loadResourceBeans(List<String> resources, ApplicationContext ctx, Environment env) {
        if (resources != null) {
            for (String resource : resources) {
                try {
                    env.jersey().register(ctx.getBean(resource));
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
                    HealthCheck healthCheckBean = (HealthCheck) ctx.getBean(healthCheck);
                    env.healthChecks().register(healthCheck, healthCheckBean);
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
                    env.getApplicationContext().manage(ctx.getBean(managed));
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
                    env.getApplicationContext().manage(ctx.getBean(lifeCycle));
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
                    env.jersey().register(ctx.getBean(provider));
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
                    env.admin().addTask((Task) ctx.getBean(task));
                } catch (NoSuchBeanDefinitionException nsbde) {
                    logNoSuchBeanDefinitionException(nsbde);
                }
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

        if (config.getJavaConfigBasePackages() != null && config.getJavaConfigBasePackages().size() > 0) {
            return initJavaConfigSpring(config, parent);
        }

        if (config.getConfigLocations() != null && config.getConfigLocations().size() > 0) {
            return initXmlSpring(parent, config);

        }

        throw new IllegalArgumentException("Configuration Error: You must specify either configLocation or javaConfigBasePackages");

    }

    private ApplicationContext initXmlSpring(ApplicationContext parent, SpringConfiguration config) {

        ApplicationContext appCtx = null;
        // Get Config Location Type.
        String cfgLocationType = config.getConfigLocationsType();

        String[] configLocations = config.getConfigLocations().toArray(new String[config.getConfigLocations().size()]);

        switch (config.getAppContextType()) {

            case WEB_APPLICATION_CONTEXT:
                // Create Web Application Context.
                appCtx = new XmlRestWebApplicationContext(configLocations, cfgLocationType, true, parent);
                break;

            case APPLICATION_CONTEXT:
                // Create Application Context.
                if (SpringConfiguration.FILE_CONFIG.equals(cfgLocationType)) {
                    appCtx = new FileSystemXmlApplicationContext(configLocations, true, parent);
                } else if (SpringConfiguration.CLASSPATH_CONFIG.equals(cfgLocationType)) {
                    appCtx = new ClassPathXmlApplicationContext(configLocations, true, parent);
                } else {
                    throw new IllegalArgumentException(MessageFormat.format("Configuration Error: configLocationsType must be either \"{0}\" or \"{1}\"", SpringConfiguration.FILE_CONFIG, SpringConfiguration.CLASSPATH_CONFIG));
                }

        }


        return appCtx;
    }

    private ApplicationContext initJavaConfigSpring(SpringConfiguration config, ApplicationContext parent) {
        String[] javaConfigBasePackages = config.getJavaConfigBasePackages().toArray(new String[config.getJavaConfigBasePackages().size()]);

        switch (config.getAppContextType()) {

            case WEB_APPLICATION_CONTEXT:
                AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new AnnotationConfigWebApplicationContext();
                annotationConfigWebApplicationContext.scan(javaConfigBasePackages);
                annotationConfigWebApplicationContext.setParent(parent);
                annotationConfigWebApplicationContext.refresh();
                return annotationConfigWebApplicationContext;

            case APPLICATION_CONTEXT:
                AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(javaConfigBasePackages);
                annotationConfigApplicationContext.setParent(parent);
                return annotationConfigApplicationContext;

        }

        throw new RuntimeException("This should never happen");

    }

    private void logNoSuchBeanDefinitionException(NoSuchBeanDefinitionException nsbde) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("Skipping missing Spring bean: ", nsbde);
        }
    }
}
