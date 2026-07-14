package com.poetry.dto;

public class LoginResponse {

    private String token;
    private String username;
    private String role;
    private boolean bindtel;
    private String email;

    public LoginResponse(String token, String username, String role, boolean bindtel, String email) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.bindtel = bindtel;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isBindtel() { return bindtel; }
    public void setBindtel(boolean bindtel) { this.bindtel = bindtel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
