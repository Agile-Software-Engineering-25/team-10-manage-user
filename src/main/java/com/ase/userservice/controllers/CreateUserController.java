package com.ase.userservice.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ase.userservice.components.GetToken;
import com.ase.userservice.entities.NewUser;

@RestController
public class CreateUserController {
	@PostMapping("/user")
	//TODO: Make le thing to create a user and make it right
	public String createUser() throws URISyntaxException, IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		NewUser newUser = new NewUser("david", "david", "daivd", "dave@fave.com", true, true);
		byte[] bytestream = newUser.getUserAsJsonBytes();
		String token = new GetToken().getToken();
	
		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users"))
    		.POST(BodyPublishers.ofByteArray(bytestream))
    		.setHeader("Content-Type", "application/json")
    		.setHeader("authorization", "bearer "+token)
    		.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
}
