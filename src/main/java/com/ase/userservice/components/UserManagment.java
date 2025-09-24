package com.ase.userservice.components;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class UserManagment {

	public static HttpResponse<String> getUserDatafromUsername(String username, String token) throws IOException, InterruptedException{
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users/?username=" + username))
    		.GET()
    		.setHeader("authorization", "bearer "+token)
    		.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response;
	}

	public static  HttpResponse<String> createUserfromJson(String newUserJson, String token) throws IOException, InterruptedException{
		HttpClient client = HttpClient.newHttpClient();
		byte[] bytestream = newUserJson.getBytes();
		HttpRequest request = HttpRequest.newBuilder()
    		.uri(URI.create("https://keycloak.sau-portal.de/admin/realms/sau/users"))
    		.POST(BodyPublishers.ofByteArray(bytestream))
    		.setHeader("Content-Type", "application/json")
    		.setHeader("authorization", "bearer "+token)
    		.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response;
	}
}
