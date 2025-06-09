/*
 * SPDX-FileCopyrightText: Copyright © 2014 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.lessons.xxe;

import java.util.Collection;
import lombok.AllArgsConstructor;
import org.owasp.webgoat.container.CurrentUser;
import org.owasp.webgoat.container.users.WebGoatUser;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nbaars
 * @since 5/4/17.
 */
@RestController
@RequestMapping("xxe/comments")
@AllArgsConstructor
public class CommentsEndpoint {

  private final CommentsCache comments;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<Collection<Comment>> retrieveComments(@CurrentUser WebGoatUser user) {
    return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noStore().mustRevalidate())
            .header("Pragma", "no-cache")
            .header("Expires", "0")
            .body(comments.getComments(user));
  }
}
