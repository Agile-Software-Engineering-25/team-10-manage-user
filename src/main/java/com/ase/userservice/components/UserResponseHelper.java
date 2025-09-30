package com.ase.userservice.components;

import com.ase.userservice.entities.UserRepresentation;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserResponseHelper {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Extracts only needed user attributes and returns as JSON string
   */
  public String extractUserAttributes(String responseBody, boolean isIdSearch) throws JsonProcessingException {
    if (responseBody == null || responseBody.trim().isEmpty()) {
      return "[]";
    }

    List<UserRepresentation> users = new ArrayList<>();

    if (isIdSearch) {
      JsonNode userNode = objectMapper.readTree(responseBody);
      users.add(extractSingleUser(userNode));
    } else {
      JsonNode usersArray = objectMapper.readTree(responseBody);
      if (usersArray.isArray()) {
        for (JsonNode userNode : usersArray) {
          users.add(extractSingleUser(userNode));
        }
      }
    }

    return objectMapper.writeValueAsString(users);
  }

  /**
   * Extracts user attributes including groups and returns as JSON string
   */
  public String extractUserAttributesWithGroups(String responseBody, boolean isIdSearch, String token)
      throws JsonProcessingException, IOException, InterruptedException {
    if (responseBody == null || responseBody.trim().isEmpty()) {
      return "[]";
    }

    List<UserRepresentation> users = new ArrayList<>();

    if (isIdSearch) {
      JsonNode userNode = objectMapper.readTree(responseBody);
      UserRepresentation user = extractSingleUser(userNode);
      user.groups = getUserGroups(user.id, token); // Rollen hinzufügen
      users.add(user);
    } else {
      JsonNode usersArray = objectMapper.readTree(responseBody);
      if (usersArray.isArray()) {
        for (JsonNode userNode : usersArray) {
          UserRepresentation user = extractSingleUser(userNode);
          user.groups = getUserGroups(user.id, token); // Rollen hinzufügen
          users.add(user);
        }
      }
    }

    return objectMapper.writeValueAsString(users);
  }

  /**
   * Extracts user attributes including both groups and roles
   */
  public String extractUserAttributesWithGroupsAndRoles(String responseBody, boolean isIdSearch, String token)
      throws JsonProcessingException, IOException, InterruptedException {
    if (responseBody == null || responseBody.trim().isEmpty()) {
      return "[]";
    }

    List<UserRepresentation> users = new ArrayList<>();

    // different kc api response format when using id vs. query parameter
    if (isIdSearch) {
      JsonNode userNode = objectMapper.readTree(responseBody);
      UserRepresentation user = extractSingleUser(userNode);
      user.groups = getUserGroups(user.id, token);
      user.roles = getUserRoles(user.id, token);
      users.add(user);
    } else {
      JsonNode usersArray = objectMapper.readTree(responseBody);
      if (usersArray.isArray()) {
        for (JsonNode userNode : usersArray) {
          UserRepresentation user = extractSingleUser(userNode);
          user.groups = getUserGroups(user.id, token);
          user.roles = getUserRoles(user.id, token);
          users.add(user);
        }
      }
    }

    return objectMapper.writeValueAsString(users);
  }

  /**
   * Gets groups for a specific user
   */
  private ArrayList<String> getUserGroups(String userId, String token)
      throws IOException, InterruptedException, JsonProcessingException {
    ArrayList<String> groups = new ArrayList<>();

    if (userId == null || userId.trim().isEmpty()) {
      return groups;
    }

    HttpClient client = HttpClient.newHttpClient();

    // Get user groups
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users/" + userId + "/groups"))
        .GET()
        .setHeader("authorization", "bearer " + token)
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      JsonNode groupsArray = objectMapper.readTree(response.body());
      if (groupsArray.isArray()) {
        for (JsonNode group : groupsArray) {
          String groupName = getStringValue(group, "name");
          if (groupName != null) {
            groups.add(groupName);
          }
        }
      }
    }

    return groups;
  }

  /**
   * Gets roles for a specific user
   */
  private ArrayList<String> getUserRoles(String userId, String token)
      throws IOException, InterruptedException, JsonProcessingException {
    ArrayList<String> roles = new ArrayList<>();

    if (userId == null || userId.trim().isEmpty()) {
      return roles;
    }

    HttpClient client = HttpClient.newHttpClient();

    // Get realm roles for the user
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users/" + userId + "/role-mappings/realm"))
        .GET()
        .setHeader("authorization", "bearer " + token)
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
      JsonNode rolesArray = objectMapper.readTree(response.body());
      if (rolesArray.isArray()) {
        for (JsonNode role : rolesArray) {
          String roleName = getStringValue(role, "name");
          // Filter out default Keycloak roles
          roles.add(roleName);
        }
      }
    }

    return roles;
  }

  /**
   * Finds group ID by group name from search response
   */
  public String findGroupIdFromResponse(String groupName, String responseBody) throws JsonProcessingException {
    if (responseBody == null || responseBody.trim().isEmpty()) {
      return null;
    }

    JsonNode groupsArray = objectMapper.readTree(responseBody);
    if (groupsArray.isArray()) {
      for (JsonNode group : groupsArray) {
        String name = getStringValue(group, "name");
        if (groupName.equalsIgnoreCase(name)) {
          return getStringValue(group, "id");
        }
      }
    }
    return null;
  }

  private UserRepresentation extractSingleUser(JsonNode userNode) {
    UserRepresentation user = new UserRepresentation();
    user.id = getStringValue(userNode, "id");
    user.username = getStringValue(userNode, "username");
    user.firstName = getStringValue(userNode, "firstName");
    user.lastName = getStringValue(userNode, "lastName");
    user.email = getStringValue(userNode, "email");
    user.groups = new ArrayList<>(); // Initialize empty groups list
    return user;
  }

  private String getStringValue(JsonNode node, String fieldName) {
    JsonNode fieldNode = node.get(fieldName);
    return fieldNode != null ? fieldNode.asText() : null;
  }
}
