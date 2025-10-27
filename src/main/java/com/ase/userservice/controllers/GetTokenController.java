package com.ase.userservice.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ase.userservice.components.GetToken;

@RestController
public class GetTokenController {

	@Autowired
	private GetToken token;

	
	@GetMapping("/token")
	public String getToken() throws IOException, InterruptedException {
		String token = this.token.getToken();
		return token;
	}

}
