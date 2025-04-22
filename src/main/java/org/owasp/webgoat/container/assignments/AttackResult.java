/*
 * SPDX-FileCopyrightText: Copyright © 2014 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.container.assignments;

import static org.apache.commons.text.StringEscapeUtils.escapeJson;

import lombok.Getter;
import org.owasp.webgoat.container.i18n.PluginMessages;
import java.util.HashMap;
import java.util.Map;

@Getter
public class AttackResult {

  private final boolean lessonCompleted;
  private final String feedback;
  private Object[] feedbackArgs;
  private final String output;
  private Object[] outputArgs;
  private final String assignment;
  private final boolean attemptWasMade;
  private final Map<String, String> headers;

  private AttackResult(
      boolean lessonCompleted,
      String feedback,
      String output,
      String assignment,
      boolean attemptWasMade) {
    this.lessonCompleted = lessonCompleted;
    this.feedback = escapeJson(feedback);
    this.output = escapeJson(output);
    this.assignment = assignment;
    this.attemptWasMade = attemptWasMade;
    this.headers = new HashMap<>();
  }

  public AttackResult(
      boolean lessonCompleted,
      String feedback,
      Object[] feedbackArgs,
      String output,
      Object[] outputArgs,
      String assignment,
      boolean attemptWasMade) {
    this.lessonCompleted = lessonCompleted;
    this.feedback = feedback;
    this.feedbackArgs = feedbackArgs;
    this.output = output;
    this.outputArgs = outputArgs;
    this.assignment = assignment;
    this.attemptWasMade = attemptWasMade;
    this.headers = new HashMap<>();
  }
  
  public AttackResult(
      boolean lessonCompleted,
      String feedback,
      Object[] feedbackArgs,
      String output,
      Object[] outputArgs,
      String assignment,
      boolean attemptWasMade,
      Map<String, String> headers) {
    this.lessonCompleted = lessonCompleted;
    this.feedback = feedback;
    this.feedbackArgs = feedbackArgs;
    this.output = output;
    this.outputArgs = outputArgs;
    this.assignment = assignment;
    this.attemptWasMade = attemptWasMade;
    this.headers = headers;
  }

  public boolean assignmentSolved() {
    return lessonCompleted;
  }

  public AttackResult apply(PluginMessages pluginMessages) {
    AttackResult attackResult = new AttackResult(
        lessonCompleted,
        pluginMessages.getMessage(feedback, feedback, feedbackArgs),
        pluginMessages.getMessage(output, output, outputArgs),
        assignment,
        attemptWasMade);
    
    // Add all headers from this AttackResult to the new one
    for (Map.Entry<String, String> header : this.headers.entrySet()) {
        attackResult.headers.put(header.getKey(), header.getValue());
    }
    
    return attackResult;
  }
}
