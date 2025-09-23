package com.ase.userservice.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ase.userservice.components.GetToken;

@RestController
@RequestMapping("/user")
public class CreateUserController {
	@PostMapping(produces = "application/json")
	public ResponseEntity<String> createUser(@RequestBody String newUserJson) throws URISyntaxException, IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		byte[] bytestream = newUserJson.getBytes();
		String token = new GetToken().getToken();
	
		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users"))
    		.POST(BodyPublishers.ofByteArray(bytestream))
    		.setHeader("Content-Type", "application/json")
    		.setHeader("authorization", "bearer "+token)
    		.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return new ResponseEntity<>(newUserJson, org.springframework.http.HttpStatus.valueOf(response.statusCode()));
	}
}
