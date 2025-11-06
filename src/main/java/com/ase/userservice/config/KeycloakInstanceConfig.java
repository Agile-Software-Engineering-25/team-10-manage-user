package com.ase.userservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakInstanceConfig {

  @Value("${keycloak.serverUrl}")
  private String keycloakServerUrl;
  @Value("${keycloak.realm}")
  private String keycloakRealm;
  @Value("${keycloak.clientId}")
  private String keycloakClientId;
  @Value("${keycloak.clientSecret}")
  private String keycloakClientSecret;

  @Bean
  public Keycloak keycloak() {
    return KeycloakBuilder.builder()
        .serverUrl(keycloakServerUrl)
        .realm(keycloakRealm) // admin realm (often "master")
        .clientId(keycloakClientId) // or your admin client
        .clientSecret(keycloakClientSecret)
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
        .build();
  }

  @Bean
  public RealmResource realm(Keycloak keycloak) {
    return keycloak.realm(keycloakRealm);
  }
}
