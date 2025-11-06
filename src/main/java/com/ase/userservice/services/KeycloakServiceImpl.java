package com.ase.userservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeyCloakService {

  private final RealmResource realm;

  @Override
  public List<UserRepresentation> getAllUsersWithRolesAndGroups() {
    return realm.users().list();
  }

  @Override
  public UserRepresentation getUserById(String id) {
    return realm.users().get(id).toRepresentation();
  }

  @Override
  public List<UserRepresentation> getUsersByGroupId(String groupId) {
    return realm.groups()
        .group(groupId)
        .members();
  }

  @Override
  public List<UserRepresentation> getUsersByGroupName(String groupName) {
    List<GroupRepresentation> groups = realm.groups()
        .groups(groupName, 0, 1); // search with exact name

    if (groups.isEmpty()) return List.of();

    String groupId = groups.getFirst().getId();
    return getUsersByGroupId(groupId);
  }

  @Override
  public List<UserRepresentation> getUsersByFilter(String username, String email) {
    List<UserRepresentation> users;

    if (username != null && !username.isBlank()) {
      users = realm.users().search(username);
    } else if (email != null && !email.isBlank()) {
      users = realm.users().searchByEmail(email, false);
    } else {
      users = realm.users().list();
    }

    return users;
  }

  /**
   * Helper method to map Keycloak UserRepresentation to custom UserRepresentation
   */
  @Override
  public com.ase.userservice.entities.UserRepresentation mapToCustomUser(UserRepresentation user, boolean excludeGroups, boolean excludeRoles) {
    var userResource = realm.users().get(user.getId());

    com.ase.userservice.entities.UserRepresentation customUser = new com.ase.userservice.entities.UserRepresentation();
    customUser.id = user.getId();
    customUser.username = user.getUsername();
    customUser.firstName = user.getFirstName();
    customUser.lastName = user.getLastName();
    customUser.email = user.getEmail();
    customUser.enabled = user.isEnabled();

    if (!excludeGroups) {
      // Fetch groups
      customUser.groups = userResource.groups()
          .stream()
          .map(GroupRepresentation::getName)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    if (!excludeRoles) {
      // Fetch realm roles
      customUser.roles = userResource.roles()
          .realmLevel()
          .listEffective()
          .stream()
          .map(RoleRepresentation::getName)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    return customUser;
  }
}
