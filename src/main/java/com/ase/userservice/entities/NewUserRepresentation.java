package com.ase.userservice.entities;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewUserRepresentation{
  public String id;
  public String username;
  public String firstName;
  public String lastName;
  public String email;
  public ArrayList<String> groups;
	public ArrayList<String> requiredActions = new ArrayList<String>(Arrays.asList("UPDATE_PASSWORD"));
	public CredentialRepresentation[] credentials = {new CredentialRepresentation()};  
}
