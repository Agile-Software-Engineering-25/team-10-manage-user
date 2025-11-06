package com.ase.userservice.controllers.v1;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ase.userservice.components.GetToken;
import com.ase.userservice.components.UserResponseHelper;

import com.fasterxml.jackson.core.JsonProcessingException;

@Slf4j
@RestController
@RequestMapping("/v1/user")
public class GetUserController {

  private final UserResponseHelper responseHelper;

  @Autowired
	private GetToken token;


  public GetUserController(UserResponseHelper responseHelper) {
    this.responseHelper = responseHelper;
  }

  @GetMapping
  public ResponseEntity<String> getUser(
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "id", required = false) String id,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "groupName", required = false) String groupName,
      @RequestParam(value = "groupId", required = false) String groupId)
      throws JsonProcessingException, IOException, InterruptedException {

    String token = this.token.getToken();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request;
    boolean isIdSearch = false;

    // get users by group ID (direct)
    if (groupId != null && !groupId.trim().isEmpty()) {
      request = HttpRequest.newBuilder()
          .uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/groups/" + groupId
              + "/members?briefRepresentation=true"))
          .GET()
          .setHeader("authorization", "bearer " + token)
          .build();
    }
    // get users by group name (search first, then get members)
    else if (groupName != null && !groupName.trim().isEmpty()) {
      // Search for group
      HttpRequest groupSearchRequest = HttpRequest.newBuilder()
          .uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/groups?search=" + groupName + "&exact=true"))
          .GET()
          .setHeader("authorization", "bearer " + token)
          .build();

      HttpResponse<String> groupResponse = client.send(groupSearchRequest, HttpResponse.BodyHandlers.ofString());
      String foundGroupId = responseHelper.findGroupIdFromResponse(groupName, groupResponse.body());

      if (foundGroupId == null) {
        return ResponseEntity.notFound().build();
      }

      request = HttpRequest.newBuilder()
          .uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/groups/" + foundGroupId
              + "/members?briefRepresentation=true"))
          .GET()
          .setHeader("authorization", "bearer " + token)
          .build();
    }
    // get user by id
    else if (id != null && !id.trim().isEmpty()) {
      isIdSearch = true;
      request = HttpRequest.newBuilder()
          .uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users/" + id))
          .GET()
          .setHeader("authorization", "bearer " + token)
          .build();
    } else {
      // build kc filter query with other options
      StringBuilder urlBuilder = new StringBuilder("https://keycloak.sau-portal.de/admin/realms/sau/users");
      boolean hasParams = false;

      // get user by username
      if (username != null && !username.trim().isEmpty()) {
        urlBuilder.append("?username=").append(username).append("&exact=true");
        hasParams = true;
      }

      // get user by email
      if (email != null && !email.trim().isEmpty()) {
        urlBuilder.append(hasParams ? "&" : "?").append("email=").append(email).append("&exact=true");
      }

      request = HttpRequest.newBuilder()
          .uri(URI.create(urlBuilder.toString()))
          .GET()
          .setHeader("authorization", "bearer " + token)
          .build();
    }

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    // Always extract with groups and roles (no parameter needed)
    String extractedResponse = responseHelper.extractUserAttributesWithGroupsAndRoles(response.body(), isIdSearch,
        token);

    return ResponseEntity.status(response.statusCode()).body(extractedResponse);
  }


}
