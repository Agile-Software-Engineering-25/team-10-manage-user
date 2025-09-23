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

@RestController
public class CreateUserController {
	@PostMapping("/user")
	//TODO: Make le thing to create a user and make it right
	public String createUser() throws URISyntaxException, IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users"))
    		.POST(BodyPublishers.ofString("{\n\t\"username\": \"testemeineapi\",\n\t\"enabled\": \"true\"\n}"))
    		.setHeader("Content-Type", "application/json")
    		.setHeader("authorization", "bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ1RXE2dlVzSHNxMEtQd2VRVmNwY2s2TGNMLU51VGNHcjhFSmJDMjhEV1Q4In0.eyJleHAiOjE3NTg2MjQ3NzIsImlhdCI6MTc1ODYyNDQ3MiwianRpIjoib25sdHJvOmY4NjZiZWE5LWIzODAtZGQ4Ni1lMmIyLTdmODIxNzYyZTAzNyIsImlzcyI6Imh0dHBzOi8va2V5Y2xvYWsuc2F1LXBvcnRhbC5kZS9yZWFsbXMvc2F1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWRtaW4tY2xpIiwic2lkIjoiODNkNGUyODItY2U1ZC00NDc0LWFjYTYtOWE4ZWY3NWM5ZjM0Iiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIn0.Sd8dPqHhz8_g5C9tn1Glq_iNoRpUnqCsg2MI-9Qhgw07yObzqD-ATGN8Zwen-eqRMxjBYbA6n0706yPJd6kTGcahgi_gARZSF2zijHHQwqML-XS_f4gF82YwF3-ZN_fwaRQCRkO8IvE49VZw2V_C8iVKMo7gisQp11pqogA3Pk6k5rbF6emy5HyxKVhwpzeqLo77qpVCTEIdRpCktoU43gdbk1pvrc8sHk0yt6_TNIYT6ExkBNDvXgpZ3egC6bWKquB_5eI5VtAwMNhky53lN5wEbUXpwDXbFD4pI7L4FM7oS6H_cy6tg6jptBsYGgt8AY0a-BhWrZpAFgwMgKCXxg")
    		.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return null;
	}
}
