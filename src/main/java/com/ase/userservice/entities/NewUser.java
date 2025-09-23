package com.ase.userservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NewUser {
	public String username;
	public String firstName;
	public String lastName;
	public String email;
	public boolean emailVerified;
	public boolean enabled = true;

	public NewUser(String username, String firstName, String lastName, String email, boolean emailVerified, boolean enabled) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.emailVerified = emailVerified;
		this.enabled = enabled;
	}

	public String JsonString() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(this);
		return json;
	}

	@JsonIgnore
	public byte[] getUserAsJsonBytes() throws JsonProcessingException {
		return this.JsonString().getBytes();
	}
}
