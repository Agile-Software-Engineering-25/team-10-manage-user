package com.ase.userservice.components;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;

import com.ase.userservice.entities.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;

public class GetToken {
	
    private String token;

	public String makehttpcall() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		String username = System.getenv("KC_USERNAME");
		String password = System.getenv("KC_PASSWORD");
		String url = "client_id=admin-cli&grant_type=password&username=%s&password=%s";

		//TODO: add credentials
		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://keycloak.sau-portal.de/realms/sau/protocol/openid-connect/token"))
    		.POST(BodyPublishers.ofString(String.format(url, username, password)))
    		.setHeader("Content-Type", "application/x-www-form-urlencoded")
    		.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		return response.body();
	}

	public String parseJson(String body) throws JsonMappingException, JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		TokenResponse jsontoken = mapper.readValue(body,TokenResponse.class);
		token = jsontoken.access_token;
		return token;
	}

    public String getToken() throws JsonMappingException, JsonProcessingException, IOException, InterruptedException {
        return parseJson(makehttpcall());
    }

}
