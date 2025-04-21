/*
 * SPDX-FileCopyrightText: Copyright © 2014 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.lessons.xxe;

import org.owasp.webgoat.container.lessons.Category;
import org.owasp.webgoat.container.lessons.Lesson;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class XXE extends Lesson {

  @Override
  public Category getDefaultCategory() {
    return Category.A5;
  }

  @Override
  public String getTitle() {
    return "xxe.title";
  }
}

@ControllerAdvice(basePackages = "org.owasp.webgoat.lessons.xxe")
class XXEControllerAdvice {
  
  @ModelAttribute
  public void addCacheControlHeaders(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");
  }
}
