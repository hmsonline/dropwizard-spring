dropwizard-spring
=================

**dropwizard-spring** provides a clean, easy way to integrate the spring framework with dropwizard-based web service projects.

With **dropwizard-spring** you configure your dropwizard components (resources, healthchecks, jersey providers, managed objects, servlets, filters, etc.) 
entirely with spring, and tell the spring service which components to enable using a simple YAML configuration.

With **dropwizard-spring** it is not necessary to subclass `com.yammer.dropwizard.Service`, instead you reference the provided 
`com.hmsonline.dropwizard.spring.SpringService` class as  your service class.


## Maven Configuration

**Maven Dependency**

Dropwizard 0.7.0:

	<dependency>
		<groupId>com.hmsonline</groupId>
		<artifactId>dropwizard-spring</artifactId>
		<version>0.6.0</version>
	</dependency>

Dropwizard 0.6.2 (dropwizard-0.6.2 branch of this repository):

	<dependency>
		<groupId>com.hmsonline</groupId>
		<artifactId>dropwizard-spring</artifactId>
		<version>0.5.1</version>
	</dependency>

**Maven Shade Plugin Configuration**

This is required to have maven build a "fat," executable jar file.

	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-shade-plugin</artifactId>
		<version>2.2</version>
		<configuration>
			<createDependencyReducedPom>true</createDependencyReducedPom>
			<filters>
				<filter>
					<artifact>*</artifact>
					<excludes>
						<exclude>META-INF/*.SF</exclude>
						<exclude>META-INF/*.RSA</exclude>
						<exclude>META-INF/*.INF</exclude>
					</excludes>
				</filter>
			</filters>
		</configuration>
		<executions>
			<execution>
				<phase>package</phase>
				<goals>
					<goal>shade</goal>
				</goals>
				<configuration>
					<transformers>
						<transformer
							implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer" />
						<transformer
							implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
							<mainClass>com.hmsonline.dropwizard.spring.SpringService</mainClass>
						</transformer>
						<transformer
							implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
							<resource>META-INF/spring.handlers</resource>
						</transformer>
						<transformer
							implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
							<resource>META-INF/spring.schemas</resource>
						</transformer>
					</transformers>
				</configuration>
			</execution>
		</executions>
	</plugin>

## Sample YAML Configuration

	# dropwizard-spring sample service configuration

	# High level information that describes the application. This can be pulled into auto documentation tools like Swagger.
	apiMetadata:
	    rootServerName: www.example.com
	    title: My Example Application
	    description: Configure and use the application via this API
	    contact: developers@example.com
	    license: License type here
	    licenseUrl: http://www.example.com/license
	    termsOfServiceUrl: http://www.example.com/tos
	
	spring:

        # Spring Context Type (Required)
        # either "web" or "app"
        appContextType: web

        # Resource Location Type (Required)
        # either "file" or "classpath"
	    configLocationsType: file
    
	    # Spring Config Locations (Required)
	    # The location of one or more beans.xml files
	    configLocations:
	       - conf/dropwizard-beans.xml

        # Servlet Filter
        # List of FilterConfiguration
        filters:
            springSecurityFilterChain:
                clazz: org.springframework.web.filter.DelegatingFilterProxy
                url: "/*"

        # List of ServletConfiguration
        servlets:
            cxf:
                clazz: org.apache.cxf.transport.servlet.CXFServlet 
                url: "/ws/soap/*"

	    # JAX-RS Resources (Required if you want your service to do anything)
	    # one or more spring beans that are JAX-RS resources
	    resources:
	       - myRestResourceBean
	       - anotherRestResourceBean
	
	    # DW Health Checks (Optional, but recommended)
	    # list of health check beans (must extend com.yammer.metrics.core.HealthCheck)
	    healthChecks:
	       - myHealthCheckBean
	
	    # Jersey Providers (Optional)
	    # list of Jersey Provider beans (ExceptionMappers, etc.)
	    jerseyProviders:
	        - myExceptionMapper
	
	    # DW Managed Objects (Optional)
	    # one or more instance of com.yammer.dropwizard.lifecycle.Managed
	    managed:
	        - myManagedObjectBean
	
	    # Jersey Life Cycles (Optional)
	    # one or more life cycle instances
	    lifeCycles:
	        - myLifeCycleBean
	    
	    # DW Tasks (Optional)
	    # one or more instance of com.yammer.dropwizard.tasks.Task
	    tasks:
	        - myDropwizardTaskBean
       

        # Enabled/Disabled Jersey Features (Optional)
        # list of Jersey features to enable/disable
        enabledJerseyFeatures:
            - com.sun.jersey.config.feature.CanonicalizeURIPath

	    disabledJerseyFeatures:
	       - com.sun.jersey.config.feature.DisableWADL


## Accessing Dropwizard Configuration Values from Spring
In some cases it may be necessary for a spring-configured component to access values in the dropwizard configuration
or the `com.yammer.dropwizard.config.Environment` instance. For example, referencing the port on which the service is configured to run.

To allow this, at startup the dropwizard-spring service injects a special "`dropwizard`" bean into the application context that contains both the dropwizard environment and configuration objects.

The spring beans.xml example below illustrates how to reference values from the dropwizard yaml configuration:

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
		xmlns:context="http://www.springframework.org/schema/context"
		xmlns:util="http://www.springframework.org/schema/util"
		xmlns:jms="http://www.springframework.org/schema/jms" xmlns:amq="http://activemq.apache.org/schema/core"
		xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
	                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
	                           http://www.springframework.org/schema/jms http://www.springframework.org/schema/jms/spring-jms.xsd
	                           http://activemq.apache.org/schema/core http://activemq.apache.org/schema/core/activemq-core.xsd
	                           http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd">

		<bean id="helloResource" class="com.hmsonline.dropwizard.spring.sample.HelloResource">
			<property name="port">
			<util:property-path path="dropwizard.configuration.httpConfiguration.port"></util:property-path>
		</property>
		</bean>
	</beans>

## Using an IDE to run your dropwizard-spring service
If you'd like to run or debug your dropwizard-spring service with an IDE (e.g. IntelliJ IDEA) follow these simple steps:
* Click `Run` -> `Edit Configurations`
* Add a new configuration (Application)
* Give it a name like Dropwizard Spring
* For `Main Class` enter `com.hmsonline.dropwizard.spring.SpringService`
* For `Program arguments` enter `server conf/config.yaml`
* For `Working directory` enter the root directory of your webservice (the location of your pom.xml)
* For `Use classpath of module` select your webservice application
* Remove `Make` from `Before launch`
* Click OK
* Now, in the menu you can click `Run` -> `Run 'Dropwizard Spring'` or `Run` -> `Debug 'Dropwizard Spring'`
