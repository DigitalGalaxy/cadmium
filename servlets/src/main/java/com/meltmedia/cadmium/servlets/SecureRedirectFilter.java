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
package com.meltmedia.cadmium.servlets;

import java.io.IOException;
import java.net.URI;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.meltmedia.cadmium.core.meta.SslRedirectConfigProcessor;

/**
 * This filter will redirect requests that do not match the security options for a given page.
 * 
 * @author John McEntire
 * @author Christian Trimble
 */
@Singleton
public class SecureRedirectFilter implements Filter {
  
  @Inject
  protected SslRedirectConfigProcessor redirect;
  
  @Inject
  protected SecureRedirectStrategy redirectStrategy;
  
  @Inject
  protected FileServlet fileServlet;
  

  void setRedirectStrategy(SecureRedirectStrategy redirectStrategy) {
    this.redirectStrategy = redirectStrategy;
  }
  
  void setRedirectConfigProcessor( SslRedirectConfigProcessor redirect ) {
    this.redirect = redirect;
  }
  
  void setFileServlet( FileServlet fileServlet ) {
    this.fileServlet = fileServlet;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  /**
   * <p>This filter takes the following actions:</p>
   * <ol>
   *   <li>It passes requests for /system or /api to the filter chain and terminates.</li>
   *   <li>If the request uses the proper protocol (http/https), then it passes the request to the filter chain and terminates.</li>
   *   <li>The filter switches the protocol by sending a 501 redirect.</li>
   * </ol>
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {
    if(redirect == null || !(req instanceof HttpServletRequest) || !(resp instanceof HttpServletResponse)) {
      chain.doFilter(req, resp);
      return;
    }
      HttpServletRequest request = (HttpServletRequest)req;
      HttpServletResponse response = (HttpServletResponse)resp;
      URI uri = URI.create(request.getRequestURL().toString());
      
      // For now, ignore the Jersey endpoints at /system and /api.
      if( uri.getPath().matches("^/(?:system|api)(/.*)$") ) {
        chain.doFilter(request, response);
        return;
      }
      
      try {
        if( !fileServlet.contentTypeOf(uri.getPath()).matches("\\Atext/html(;.*)?\\Z")) {
          chain.doFilter(request, response);
          return;
        }
      }
      catch( Exception e ) {
        chain.doFilter(request, response);
        return;
      }
      
      try {
        // determine the request security and what we expect.
        boolean shouldBeSecure = redirect.shouldBeSsl(uri.getPath());
        
        // if we don't support the protocol or the security is correct, pass the request through.
        if( redirectStrategy.isSecure(request) == shouldBeSecure ) {
          chain.doFilter(request, response);
          return;
        }
        else if( shouldBeSecure ) {
          redirectStrategy.makeSecure(request, response);
          return;
        }
        else {
          redirectStrategy.makeInsecure(request, response);
          return;
        }
      }
      catch( UnsupportedProtocolException upe ) {
        chain.doFilter(request, response);
        return;        
      }
    }

  @Override
  public void destroy() {

  }


}
