package com.ase.userservice.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ase.userservice.components.GetToken;

@RestController
@RequestMapping("/user")
public class DeleteUserController {
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> deleteUser(@PathVariable String id) throws IOException, InterruptedException {
        String token = new GetToken().getToken();
        
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users/" + id))
            .DELETE()
            .setHeader("Authorization", "Bearer " + token)
             .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ResponseEntity<>(response.body(), org.springframework.http.HttpStatus.valueOf(response.statusCode()));
    }
}
