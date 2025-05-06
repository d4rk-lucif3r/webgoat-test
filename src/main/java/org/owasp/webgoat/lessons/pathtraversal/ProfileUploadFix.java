/*
 * SPDX-FileCopyrightText: Copyright © 2020 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.lessons.pathtraversal;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.owasp.webgoat.container.CurrentUsername;
import org.owasp.webgoat.container.assignments.AssignmentHints;
import org.owasp.webgoat.container.assignments.AttackResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;
import java.io.File;

@RestController
@AssignmentHints({
  "path-traversal-profile-fix.hint1",
  "path-traversal-profile-fix.hint2",
  "path-traversal-profile-fix.hint3"
})
public class ProfileUploadFix extends ProfileUploadBase {

  public ProfileUploadFix(@Value("${webgoat.server.directory}") String webGoatHomeDirectory) {
    super(webGoatHomeDirectory);
  }

  /**
   * Sanitizes file name to prevent path traversal attacks
   * - Removes any directory traversal sequences (../, ..\, etc.)
   * - Extracts just the filename without any path components
   * - Validates that the resulting path is safe
   */
  private String sanitizeFileName(String input) {
    if (input == null) {
      return "";
    }
    
    // Get only the filename without any path
    String fileName = new File(input).getName();
    
    // Remove any potential directory traversal sequences just to be safe
    fileName = fileName.replaceAll("\\.\\./|\\.\\\\", "");
    fileName = fileName.replaceAll("\\.\\.\\\\|\\.\\.\\\\", "");
    
    // Additional security check - verify the resulting path doesn't contain traversal attempts
    try {
      Path normalizedPath = Paths.get(fileName).normalize();
      if (!normalizedPath.toString().equals(fileName)) {
        return ""; // Path still contains traversal attempts after normalization
      }
    } catch (InvalidPathException e) {
      return ""; // Invalid path, return empty string
    }
    
    return fileName;
  }

  @PostMapping(
      value = "/PathTraversal/profile-upload-fix",
      consumes = ALL_VALUE,
      produces = APPLICATION_JSON_VALUE)
  @ResponseBody
  public AttackResult uploadFileHandler(
      @RequestParam("uploadedFileFix") MultipartFile file,
      @RequestParam(value = "fullNameFix", required = false) String fullName,
      @CurrentUsername String username) {
    // Apply thorough filename sanitization to prevent path traversal vulnerabilities
    String sanitizedName = sanitizeFileName(fullName);
    return super.execute(file, sanitizedName, username);
  }

  @GetMapping("/PathTraversal/profile-picture-fix")
  @ResponseBody
  public ResponseEntity<?> getProfilePicture(@CurrentUsername String username) {
    return super.getProfilePicture(username);
  }
}
