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

		//String username = System.getenv("KC_USERNAME");
		String client_id = "team-10";
		//String password = System.getenv("KC_PASSWORD");
		String client_secret = "wpJ15X9VuW8ACTzWXzXiYhBIc4CGsIjZ";
		String url = "client_id=%s&grant_type=client_credentials&client_secret=%s";

		//TODO: add credentials
		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://keycloak.sau-portal.de/realms/sau/protocol/openid-connect/token"))
    		.POST(BodyPublishers.ofString(String.format(url, client_id, client_secret)))
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
