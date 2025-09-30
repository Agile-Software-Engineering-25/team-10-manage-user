package com.ase.userservice.components;

import com.ase.userservice.entities.UserRepresentation;

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
    return user;
  }

  private String getStringValue(JsonNode node, String fieldName) {
    JsonNode fieldNode = node.get(fieldName);
    return fieldNode != null ? fieldNode.asText() : null;
  }

}
