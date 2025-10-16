package com.ase.userservice.entities;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRepresentation {
  public String id;
  public String username;
  public String firstName;
  public String lastName;
  public String email;
  public ArrayList<String> groups;
  //public ArrayList<String> roles;
  public boolean enabled = true;
}
