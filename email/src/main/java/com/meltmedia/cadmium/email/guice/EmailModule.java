/**
 *    Copyright 2012 meltmedia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.meltmedia.cadmium.email.guice;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.meltmedia.cadmium.core.CadmiumModule;
import com.meltmedia.cadmium.email.jersey.EmailResource;


@CadmiumModule
public class EmailModule extends AbstractModule {
  private final Logger log = LoggerFactory.getLogger(getClass());
	
  // Email config
  private String mailJNDIName;
  private String mailSessionStrategy;
  private String mailMessageTransformer;
  private Properties emailProperties;
	
	@Override
	protected void configure() {
	// bind Epsilon 
		emailProperties = loadProperties(new Properties(), log);
    mailJNDIName = emailProperties.getProperty("com.meltmedia.email.jndi");
    mailMessageTransformer = emailProperties.getProperty("melt.mail.messagetransformer");
    mailSessionStrategy = emailProperties.getProperty("melt.mail.sessionstrategy");
		
		// bind email services
    bind(String.class).annotatedWith(Names.named("com.meltmedia.email.jndi")).toInstance(mailJNDIName);
    bind(String.class).annotatedWith(Names.named("melt.mail.messagetransformer")).toInstance(mailMessageTransformer);
    bind(String.class).annotatedWith(Names.named("melt.mail.sessionstrategy")).toInstance(mailSessionStrategy);
    bind(com.meltmedia.cadmium.email.internal.EmailServiceImpl.class).asEagerSingleton();
    bind(EmailResource.class).asEagerSingleton();
		
	}
	
	 public static Properties loadProperties( Properties properties, Logger log ) {
	    InputStream reader = null;
	    try{
	      reader = EmailModule.class.getClassLoader().getResourceAsStream("email.properties");
	      properties.load(reader);
	    } catch(Exception e) {
	      log.warn("Failed to load ");
	    } finally {
	      IOUtils.closeQuietly(reader);
	    }
	    return properties;
	  }

}
