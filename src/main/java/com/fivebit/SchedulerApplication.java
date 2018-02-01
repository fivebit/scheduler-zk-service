package com.fivebit;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Registers the components to be used by the JAX-RS application
 * 
 * @author fivebit
 * 
 */
public class SchedulerApplication extends ResourceConfig {

	/**
	 * Register JAX-RS application components.
	 */
	public SchedulerApplication() {
        packages("com.fivebit");
		register(JacksonFeature.class);
		register(EntityFilteringFeature.class);
	}
}
