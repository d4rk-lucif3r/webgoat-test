/*
 * SPDX-FileCopyrightText: Copyright © 2014 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.lessons.xxe;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller advice to add cache control headers to all XXE lesson endpoints
 * to prevent sensitive information in the XXE lesson from being cached by browsers.
 * This addresses the "Sensitive Pages Could Be Cached" vulnerability.
 */
@ControllerAdvice(basePackages = "org.owasp.webgoat.lessons.xxe")
public class XXECacheControlConfig {
    
    /**
     * Add cache control headers to all responses in the XXE lesson
     */
    @ModelAttribute
    public void addCacheControlHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}