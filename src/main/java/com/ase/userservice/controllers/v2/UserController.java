package com.ase.userservice.controllers.v2;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ase.userservice.entities.UserRepresentation;
import com.ase.userservice.services.KeyCloakService;
import com.ase.userservice.services.KeycloakServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v2/user")
@RequiredArgsConstructor
public class UserController {

  private final KeyCloakService keycloakService;

  @GetMapping
  public ResponseEntity<List<UserRepresentation>> getUser(
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "id", required = false) String id,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "groupName", required = false) String groupName,
      @RequestParam(value = "groupId", required = false) String groupId,
      @RequestParam(value = "excludeRoles", required = false, defaultValue = "false") boolean excludeRoles,
      @RequestParam(value = "excludeGroups", required = false, defaultValue = "false") boolean excludeGroups) {

    List<org.keycloak.representations.idm.UserRepresentation> users;

    if (groupId != null && !groupId.isBlank()) {
      users = keycloakService.getUsersByGroupId(groupId);
    } else if (groupName != null && !groupName.isBlank()) {
      users = keycloakService.getUsersByGroupName(groupName);
    } else if (id != null && !id.isBlank()) {
      var user = keycloakService.getUserById(id);
      users = user != null ? List.of(user) : List.of();
    } else {
      users = keycloakService.getUsersByFilter(username, email);
    }

    if (users.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(users.parallelStream()
        .map(user -> keycloakService.mapToCustomUser(user, excludeGroups, excludeRoles))
        .toList());
  }
}
