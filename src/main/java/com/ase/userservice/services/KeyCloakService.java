package com.ase.userservice.services;

import org.keycloak.representations.idm.UserRepresentation;
import java.util.List;

public interface KeyCloakService {
  List<UserRepresentation> getAllUsersWithRolesAndGroups(); // existing method

  UserRepresentation getUserById(String id);

  List<UserRepresentation> getUsersByGroupId(String groupId);

  List<UserRepresentation> getUsersByGroupName(String groupName);

  List<UserRepresentation> getUsersByFilter(String username, String email);

  com.ase.userservice.entities.UserRepresentation mapToCustomUser(UserRepresentation kcUser, boolean excludeGroups, boolean excludeRoles);
}
