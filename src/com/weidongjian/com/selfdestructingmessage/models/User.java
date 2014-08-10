package com.weidongjian.com.selfdestructingmessage.models;

public class User {
	private String ID;
	private String name;
	private String email;
	
	public User(String id, String name, String email) {
		ID = id;
		this.name = name;
		this.email = email;
	}
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
