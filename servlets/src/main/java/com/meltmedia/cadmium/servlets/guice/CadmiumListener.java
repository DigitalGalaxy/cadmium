package com.meltmedia.cadmium.servlets.guice;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.MessageListener;
import org.jgroups.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.meltmedia.cadmium.core.CommandAction;
import com.meltmedia.cadmium.core.ContentService;
import com.meltmedia.cadmium.core.CoordinatedWorker;
import com.meltmedia.cadmium.core.SiteDownService;
import com.meltmedia.cadmium.core.commands.CommandMapProvider;
import com.meltmedia.cadmium.core.commands.CurrentStateCommandAction;
import com.meltmedia.cadmium.core.commands.StateUpdateCommandAction;
import com.meltmedia.cadmium.core.commands.SyncCommandAction;
import com.meltmedia.cadmium.core.commands.UpdateCommandAction;
import com.meltmedia.cadmium.core.commands.UpdateDoneCommandAction;
import com.meltmedia.cadmium.core.commands.UpdateFailedCommandAction;
import com.meltmedia.cadmium.core.git.GitService;
import com.meltmedia.cadmium.core.lifecycle.LifecycleService;
import com.meltmedia.cadmium.core.messaging.ChannelMember;
import com.meltmedia.cadmium.core.messaging.MembershipTracker;
import com.meltmedia.cadmium.core.messaging.MessageReceiver;
import com.meltmedia.cadmium.core.messaging.MessageSender;
import com.meltmedia.cadmium.core.messaging.ProtocolMessage;
import com.meltmedia.cadmium.core.messaging.jgroups.JChannelProvider;
import com.meltmedia.cadmium.core.messaging.jgroups.JGroupsMessageSender;
import com.meltmedia.cadmium.core.messaging.jgroups.MultiClassReceiver;
import com.meltmedia.cadmium.core.worker.CoordinatedWorkerImpl;
import com.meltmedia.cadmium.servlets.FileServlet;
import com.meltmedia.cadmium.servlets.MaintenanceFilter;
import com.meltmedia.cadmium.servlets.jersey.UpdateService;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Builds the context with the Guice framework.  To see how this works, go to:
 * http://code.google.com/p/google-guice/wiki/ServletModule
 * 
 * @author Christian Trimble
 */

public class CadmiumListener extends GuiceServletContextListener {
  private final Logger log = LoggerFactory.getLogger(getClass());
  public static final String CONFIG_PROPERTIES_FILE = "config.properties";
  public static final String BASE_PATH_ENV = "com.meltmedia.cadmium.contentRoot";
  public static final String LAST_UPDATED_DIR = "com.meltmedia.cadmium.lastUpdated";
  private String applicationBasePath = "/Library/WebServer/Cadmium";
  private String repoDir = "git-checkout";
  private String contentDir = "renderedContent";
	Injector injector = null;
	private List<ChannelMember> members;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	  JChannel channel = injector.getInstance(JChannel.class);
	  if(channel != null) {
	    channel.close();
	  }
	  GitService git = injector.getInstance(GitService.class);
	  if(git != null) {
	    try {
        git.close();
      } catch (Exception e) {
        log.warn("Failed to close GitService", e);
      }
	  }
	  super.contextDestroyed(event);
	}

	@Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
	  String applicationBasePath = servletContextEvent.getServletContext().getInitParameter("applicationBasePath");
	  if(applicationBasePath != null && applicationBasePath.trim().length() > 0) {
	    File appBasePath = new File(applicationBasePath);
	    if(appBasePath.isDirectory() && appBasePath.canWrite()) {
	      this.applicationBasePath = applicationBasePath;
	    }
	  }
	  Properties configProperties = new Properties();
    configProperties.putAll(System.getenv());
    configProperties.putAll(System.getProperties());
    
    if(configProperties.containsKey(BASE_PATH_ENV) ) {
      File basePath = new File(configProperties.getProperty(BASE_PATH_ENV));
      if(basePath.exists() && basePath.canRead() && basePath.canWrite()) {
        this.applicationBasePath = basePath.getAbsolutePath();
      }
    }
	  String repoDir = servletContextEvent.getServletContext().getInitParameter("repoDir");
	  if(repoDir != null && repoDir.trim().length() > 0) {
	    this.repoDir = repoDir;
	  }
	  String contentDir = servletContextEvent.getServletContext().getInitParameter("contentDir");
	  if(contentDir != null && contentDir.trim().length() > 0) {
	    this.contentDir = contentDir;
	  }
    File repoFile = new File(this.applicationBasePath, this.repoDir);
    if(repoFile.isDirectory() && repoFile.canWrite()) {
      this.repoDir = repoFile.getAbsoluteFile().getAbsolutePath();
    }
    File contentFile = new File(this.applicationBasePath, this.contentDir);
    if(contentFile.isDirectory() && contentFile.canWrite()) {
      this.contentDir = contentFile.getAbsoluteFile().getAbsolutePath();
    }
    super.contextInitialized(servletContextEvent);
  }

  @Override
	protected Injector getInjector() {
		injector = Guice.createInjector(createServletModule());
		return injector;
	}
	
	private ServletModule createServletModule() {
      return new ServletModule() {
        @Override
		protected void configureServlets() {
          
          Properties configProperties = new Properties();
          configProperties.putAll(System.getenv());
          configProperties.putAll(System.getProperties());
          
          if(new File(applicationBasePath, CONFIG_PROPERTIES_FILE).exists()) {
            try{
              configProperties.load(new FileReader(new File(applicationBasePath, CONFIG_PROPERTIES_FILE)));
            } catch(Exception e){
              log.warn("Failed to load properties file ["+CONFIG_PROPERTIES_FILE+"] from content directory.", e);
            }
          }
          
          if(configProperties.containsKey(LAST_UPDATED_DIR)) {
            File cntDir = new File(configProperties.getProperty(LAST_UPDATED_DIR));
            if(cntDir.exists() && cntDir.canRead()) {
              contentDir = cntDir.getAbsolutePath();
            }
          }

          bind(MaintenanceFilter.class).in(Scopes.SINGLETON);
          bind(SiteDownService.class).to(MaintenanceFilter.class);
          
          bind(FileServlet.class).in(Scopes.SINGLETON);
          bind(ContentService.class).to(FileServlet.class);
          
          bind(MessageSender.class).to(JGroupsMessageSender.class);
          
          try {
            bind(GitService.class).toInstance(GitService.createGitService(repoDir));
          } catch (Exception e) {
            throw new Error("Failed to bind git service");
          }
          
          members = Collections.synchronizedList(new ArrayList<ChannelMember>());
          bind(new TypeLiteral<List<ChannelMember>>(){}).annotatedWith(Names.named("members")).toInstance(members);
                    
          bind(CommandAction.class).annotatedWith(Names.named(ProtocolMessage.CURRENT_STATE.name())).to(CurrentStateCommandAction.class).in(Scopes.SINGLETON);
          bind(CommandAction.class).annotatedWith(Names.named(ProtocolMessage.STATE_UPDATE.name())).to(StateUpdateCommandAction.class).in(Scopes.SINGLETON);
          bind(CommandAction.class).annotatedWith(Names.named(ProtocolMessage.SYNC.name())).to(SyncCommandAction.class).in(Scopes.SINGLETON);
          bind(CommandAction.class).annotatedWith(Names.named(ProtocolMessage.UPDATE.name())).to(UpdateCommandAction.class).in(Scopes.SINGLETON);
          bind(CommandAction.class).annotatedWith(Names.named(ProtocolMessage.UPDATE_DONE.name())).to(UpdateDoneCommandAction.class).in(Scopes.SINGLETON);
          bind(CommandAction.class).annotatedWith(Names.named(ProtocolMessage.UPDATE_FAILED.name())).to(UpdateFailedCommandAction.class).in(Scopes.SINGLETON);
          
          
          bind(new TypeLiteral<Map<ProtocolMessage, CommandAction>>(){}).annotatedWith(Names.named("commandMap")).toProvider(CommandMapProvider.class);
          
          Map<String, String> fileParams = new HashMap<String, String>();
          fileParams.put("basePath", contentDir);
          
          Map<String, String> maintParams = new HashMap<String, String>();
          maintParams.put("ignorePrefix", "/system");

          serve("/system/*").with(GuiceContainer.class);
          
          serve("/*").with(FileServlet.class, fileParams);
          
          filter("/*").through(MaintenanceFilter.class, maintParams);

          //Bind channel name
          bind(String.class).annotatedWith(Names.named(JChannelProvider.CHANNEL_NAME)).toInstance("CadmiumChannel-v2.0");
          
          bind(Properties.class).annotatedWith(Names.named(CONFIG_PROPERTIES_FILE)).toInstance(configProperties);
          
          //Bind Config file URL
          URL propsUrl = JChannelProvider.class.getClassLoader().getResource("tcp.xml");
          bind(URL.class).annotatedWith(Names.named(JChannelProvider.CONFIG_NAME)).toInstance(propsUrl);
          
          //Bind JChannel provider
          bind(JChannel.class).toProvider(JChannelProvider.class).in(Scopes.SINGLETON);
          

          bind(MembershipListener.class).to(MembershipTracker.class);
          bind(MessageListener.class).to(MessageReceiver.class);
          
          bind(LifecycleService.class);
          bind(CoordinatedWorker.class).to(CoordinatedWorkerImpl.class);
          
          bind(Receiver.class).to(MultiClassReceiver.class).asEagerSingleton();
          
          //Bind Jersey UpdateService
          bind(UpdateService.class).asEagerSingleton();
		}
      };
	}
}
