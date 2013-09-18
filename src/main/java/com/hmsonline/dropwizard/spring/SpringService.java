// Copyright (c) 2012 Health Market Science, Inc.
package com.hmsonline.dropwizard.spring;

import java.util.List;

import org.eclipse.jetty.util.component.LifeCycle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.dropwizard.tasks.Task;
import com.yammer.metrics.core.HealthCheck;

public class SpringService extends Service<SpringServiceConfiguration> {
	
	private String serviceName = "<unknown>"; 

    public static void main(String[] args) throws Exception {
    	SpringService springService = new SpringService();
    	springService.setServiceName("dropwizard-spring");
        springService.run(args);
    }

    public void setServiceName(String serviceName) {
    	this.serviceName = serviceName;
    }

    @Override
    public void initialize(Bootstrap<SpringServiceConfiguration> bootstrap) {
    	bootstrap.setName(serviceName);
    	// This is needed to avoid an exception when deserializing Json to an ArrayList<String>
    	bootstrap.getObjectMapperFactory().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }
    
    @Override
    public void run(SpringServiceConfiguration configuration, Environment environment) {

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

        enableJerseyFeatures(config.getEnabledJerseyFeatures(), environment);
        disableJerseyFeatures(config.getDisabledJerseyFeatures(), environment);

    }

    private void loadResourceBeans(List<String> resources, ApplicationContext ctx, Environment env) {
        if (resources != null) {
            for (String resource : resources) {
                env.addResource(ctx.getBean(resource));
            }
        }

    }

    private void loadHealthCheckBeans(List<String> healthChecks, ApplicationContext ctx, Environment env) {
        if (healthChecks != null) {
            for (String healthCheck : healthChecks) {
                env.addHealthCheck((HealthCheck) ctx.getBean(healthCheck));
            }
        }
    }

    private void loadManagedBeans(List<String> manageds, ApplicationContext ctx, Environment env) {
        if (manageds != null) {
            for (String managed : manageds) {
                env.manage((Managed) ctx.getBean(managed));
            }
        }
    }

    private void loadLifeCycleBeans(List<String> lifeCycles, ApplicationContext ctx, Environment env) {
        if (lifeCycles != null) {
            for (String lifeCycle : lifeCycles) {
                env.manage((LifeCycle) ctx.getBean(lifeCycle));
            }
        }
    }

    private void loadJerseyProviders(List<String> providers, ApplicationContext ctx, Environment env) {
        if (providers != null) {
            for (String provider : providers) {
                env.addProvider(ctx.getBean(provider));
            }
        }
    }

    private void loadTasks(List<String> tasks, ApplicationContext ctx, Environment env) {
        if (tasks != null) {
            for (String task : tasks) {
                env.addTask((Task) ctx.getBean(task));
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

    private ApplicationContext initSpringParent() {
        ApplicationContext parent = new ClassPathXmlApplicationContext(
                new String[] { "dropwizardSpringApplicationContext.xml" }, true);
        return parent;
    }

    private ApplicationContext initSpring(SpringConfiguration config, ApplicationContext parent) {
        ApplicationContext appCtx = null;
        String ctxType = config.getAppContextType();
        String[] configLocations = config.getConfigLocations().toArray(new String[config.getConfigLocations().size()]);

        if (ctxType.equals("file")) {
            appCtx = new FileSystemXmlApplicationContext(configLocations, true, parent);
        } else if (ctxType.equals("classpath")) {
            appCtx = new ClassPathXmlApplicationContext(configLocations, true, parent);
        } else {
            throw new IllegalArgumentException(
                    "Configuration Error: appContextType must be either 'file' or 'classpath'");
        }
        return appCtx;
    }

}
