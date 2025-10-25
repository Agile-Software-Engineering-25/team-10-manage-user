package com.ase.userservice.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ase.userservice.components.GetToken;
import com.ase.userservice.components.UserManagment;
import com.ase.userservice.entities.NewUserRepresentation;

@RestController
@RequestMapping("/user")
public class CreateUserController {
	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createUser(@RequestBody NewUserRepresentation newUser) throws URISyntaxException, IOException, InterruptedException {
		
		String token = new GetToken().getToken();

		String newUserAsJson = new ObjectMapper().writeValueAsString(newUser);
		HttpResponse<String> response = UserManagment.createUserfromJson(newUserAsJson, token);
		if (response.statusCode() != 201)
			return new ResponseEntity<>(response.body(), org.springframework.http.HttpStatus.valueOf(response.statusCode()));
		String username = newUser.email;
		response = UserManagment.getUserDatafromUsername(username, token);

		int responseCode = sendmail(newUser.email, username, newUser.credentials[0].value);
		if (responseCode == 204) {
			System.out.println("Email sent successfully to " + newUser.email);
		} else {
			System.out.printf("Failed to send email to %s with %d\n", newUser.email, responseCode);
		}

		return new ResponseEntity<>(response.body(), org.springframework.http.HttpStatus.valueOf(response.statusCode()));
	}

	public int sendmail(String mail, String name, String password) throws IOException, InterruptedException{
		HttpClient client = HttpClient.newHttpClient();
		
		String token = new GetToken().getToken();

		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://sau-portal.de/notification-service/api/v1/emails"))
    		.header("Authorization", "Bearer " + token)
    		.header("Content-Type", "application/json")
    		.method("POST", HttpRequest.BodyPublishers.ofString(String.format("{to\": [\n    \"%s\"\n  ],\n  \"subject\": \"Initiale Anmeldedaten\",\n  \"template\": \"GENERIC\",\n  \"variables\": {\n    \"header\": \"Zugang zu Ihrem Konto\",\n    \"name\": \"%s\",\n    \"body\": [\n      \"Ihre Anmeldedaten stehen bereit!\",\n      \"E-Mail: %s\",\n      \"Passwort: %s\"\n    ],\n    \"highlightLine\": \"Bitte melden Sie sich mit Ihren Zugangsdaten im Portal an und folgen Sie den dortigen Anweisungen.\",\n    \"ctaUrl\": \"https://sau-portal.de/\",\n    \"ctaLabel\": \"Zum Portal\",\n    \"note\": \"Automatisch generierte E-Mail. Bitte nicht antworten.\"\n  }\n}", mail, name, mail, password)))
    		.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.statusCode();
	}

}
