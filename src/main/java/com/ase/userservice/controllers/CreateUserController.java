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
		// HttpResponse<String> response = createUserfromJson(newUserAsJson, token);
		if (response.statusCode() != 201)
			return new ResponseEntity<>(response.body(), org.springframework.http.HttpStatus.valueOf(response.statusCode()));
		String username = newUser.username;
		response = UserManagment.getUserDatafromUsername(username, token);

		if (sendmail(newUser.email, username, newUser.credentials[0].value) == 204) {
			System.out.println("Email sent successfully to " + newUser.email);
		} else {
			System.out.println("Failed to send email to " + newUser.email);
		}

		// response = getUserDatafromUsername(username, token);
		return new ResponseEntity<>(response.body()+"\n\"init-password\": "+ "\"" +newUser.credentials[0].value + "\"", org.springframework.http.HttpStatus.valueOf(response.statusCode()));
	}

	public int sendmail(String mail, String name, String password) throws IOException, InterruptedException{
		HttpClient client = HttpClient.newHttpClient();
		
		String token = new GetToken().getClientToken();

		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://sau-portal.de/notification-service/api/v1/emails"))
    		.header("Authorization", "Bearer " + token)
    		.header("Content-Type", "application/json")
    		.method("POST", HttpRequest.BodyPublishers.ofString("{   \n\t\"to\": [     \n\t\t\"" + mail + "\"   ],\n\t\"subject\": \"Ihre Anmeldedaten\",   \n\t\"template\": \"WELCOME\",   \n\t\"variables\": {     \n\t\t\"name\": \"" + name + "\",     \n\t\t\"code\": \"" + password + "\"   \n\t} \n}"))
    		.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.statusCode();
	}

}
