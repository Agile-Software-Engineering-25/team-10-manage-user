package com.ase.userservice.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ase.userservice.components.GetToken;
import com.ase.userservice.components.UserManagment;
import com.ase.userservice.entities.NewUser;

@RestController
@RequestMapping("/user")
public class CreateUserController {
	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createUser(@RequestBody NewUser newUser) throws URISyntaxException, IOException, InterruptedException {
		String token = new GetToken().getToken();

		String newUserAsJson = new ObjectMapper().writeValueAsString(newUser);
		HttpResponse<String> response = UserManagment.createUserfromJson(newUserAsJson, token);
		// HttpResponse<String> response = createUserfromJson(newUserAsJson, token);
		if (response.statusCode() != 201)
			return new ResponseEntity<>(response.body(), org.springframework.http.HttpStatus.valueOf(response.statusCode()));
		String username = newUser.username;
		response = UserManagment.getUserDatafromUsername(username, token);
		// response = getUserDatafromUsername(username, token);
		return new ResponseEntity<>(response.body()+"\n\"init-password\": "+ "\"" +newUser.credentials[0].value + "\"", org.springframework.http.HttpStatus.valueOf(response.statusCode()));
	}
	// public HttpResponse<String> getUserDatafromUsername(String username, String token) throws IOException, InterruptedException{
	// 	HttpClient client = HttpClient.newHttpClient();
	// 	HttpRequest request = HttpRequest.newBuilder()
    // 		.uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users/?username=" + username))
    // 		.GET()
    // 		.setHeader("authorization", "bearer "+token)
    // 		.build();
	// 	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	// 	return response;
	// }

	// public  HttpResponse<String> createUserfromJson(String newUserJson, String token) throws IOException, InterruptedException{
	// 	HttpClient client = HttpClient.newHttpClient();
	// 	byte[] bytestream = newUserJson.getBytes();
	// 	HttpRequest request = HttpRequest.newBuilder()
    // 		.uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users"))
    // 		.POST(BodyPublishers.ofByteArray(bytestream))
    // 		.setHeader("Content-Type", "application/json")
    // 		.setHeader("authorization", "bearer "+token)
    // 		.build();
	// 	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	// 	return response;
	// }
}
