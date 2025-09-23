package com.ase.userservice.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
public class GetTokenController {
	
	@GetMapping("/token")
	public String getToken() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		//TODO: add credentials
		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://keycloak.sau-portal.de/realms/sau/protocol/openid-connect/token"))
    		.POST(BodyPublishers.ofString("client_id=admin-cli&grant_type=password&username=<your_user>&password=<your_password>"))
    		.setHeader("Content-Type", "application/x-www-form-urlencoded")
    		.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		return response.body();
	}

}
