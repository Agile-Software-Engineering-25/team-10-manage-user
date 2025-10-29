package com.ase.userservice.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

	@Autowired
	private GetToken token;

	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createUser(@RequestBody NewUserRepresentation newUser) throws URISyntaxException, IOException, InterruptedException {

		String token = this.token.getToken();

		String newUserAsJson = new ObjectMapper().writeValueAsString(newUser);
		HttpResponse<String> response = UserManagment.createUserfromJson(newUserAsJson, token);
		if (response.statusCode() != 201) {
			return new ResponseEntity<>(response.body(), org.springframework.http.HttpStatus.valueOf(response.statusCode()));
		}

		String username = newUser.email;
		response = UserManagment.getUserDatafromUsername(username, token);

		int responseCode = sendmail(newUser.email, username, newUser.credentials[0].value);
		if (responseCode == 204) {
			log.debug("Email sent successfully to %s", newUser.email);
		} else {
			log.error("Failed to send email to %s with %d\n", newUser.email, responseCode);
		}

		return new ResponseEntity<>(response.body(), org.springframework.http.HttpStatus.valueOf(response.statusCode()));
	}

	public int sendmail(String mail, String name, String password) throws IOException, InterruptedException{
		HttpClient client = HttpClient.newHttpClient();

		String token = this.token.getToken();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> payload = new HashMap<>();
		payload.put("to", List.of(mail));
		payload.put("subject", "Initiale Anmeldedaten");
		payload.put("template", "GENERIC");

		Map<String, Object> variables = new LinkedHashMap<>();
		variables.put("header", "Zugang zu Ihrem Konto");
		variables.put("name", name);
		List<String> body = new ArrayList<>();
		body.add("Ihre Anmeldedaten stehen bereit!");
		body.add("E-Mail: " + mail);
		body.add("Passwort: " + password);
		variables.put("body", body);
		variables.put("highlightLine", "Bitte melden Sie sich mit Ihren Zugangsdaten im Portal an und folgen Sie den dortigen Anweisungen.");
		variables.put("ctaUrl", "https://sau-portal.de/");
		variables.put("ctaLabel", "Zum Portal");
		variables.put("note", "Automatisch generierte E-Mail. Bitte nicht antworten.");

		payload.put("variables", variables);

		String jsonBody = mapper.writeValueAsString(payload);

		HttpRequest request = HttpRequest.newBuilder()
	    	.uri(URI.create("https://sau-portal.de/notification-service/api/v1/emails"))
	    	.header("Authorization", "Bearer " + token)
	    	.header("Content-Type", "application/json")
	    	.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
	    	.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.statusCode();
	}

}
