/*
 * SPDX-FileCopyrightText: Copyright © 2017 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.webwolf;

import jakarta.annotation.PostConstruct;
import java.io.File;
import org.owasp.webgoat.container.UserInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author nbaars
 * @since 8/13/17.
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

  @Value("${webwolf.fileserver.location}")
  private String fileLocation;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/files/**")
        .addResourceLocations("file:///" + fileLocation + "/")
        .setCachePeriod(0);

    registry.addResourceHandler("/css/**")
        .addResourceLocations("classpath:/webwolf/static/css/")
        .setCachePeriod(0);
    registry.addResourceHandler("/js/**")
        .addResourceLocations("classpath:/webwolf/static/js/")
        .setCachePeriod(0);
    registry
        .addResourceHandler("/images/**")
        .addResourceLocations("classpath:/webwolf/static/images/")
        .setCachePeriod(0);
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("webwolf-login");
    registry.addViewController("/home").setViewName("home");
    registry.addViewController("/").setViewName("home");
  }

  @Bean
  public CacheControlHeadersInterceptor cacheControlHeadersInterceptor() {
    return new CacheControlHeadersInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new UserInterceptor());
    registry.addInterceptor(cacheControlHeadersInterceptor());
  }

  @PostConstruct
  public void createDirectory() {
    File file = new File(fileLocation);
    if (!file.exists()) {
      file.mkdirs();
    }
  }
}

/**
 * Interceptor to add Cache-Control headers to all responses
 * to prevent sensitive pages from being cached by the browser
 */
@Component
class CacheControlHeadersInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Set cache control headers for all requests
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        return true;
    }
}
