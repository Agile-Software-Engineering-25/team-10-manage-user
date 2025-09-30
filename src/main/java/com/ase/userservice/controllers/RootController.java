package com.ase.userservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

	@GetMapping("/userapi")
	public ResponseEntity<String> root() {
		return ResponseEntity.ok("API Root: /userapi/");
	}

}
