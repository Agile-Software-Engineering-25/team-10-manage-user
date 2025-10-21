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
		

		return new ResponseEntity<>(response.body(), org.springframework.http.HttpStatus.valueOf(response.statusCode()));
	}
}
