package com.poetry.dto;

public class LoginResponse {

    private String token;
    private String username;
    private boolean bindwx;
    private boolean bindtel;

    public LoginResponse(String token, String username, boolean bindwx, boolean bindtel) {
        this.token = token;
        this.username = username;
        this.bindwx = bindwx;
        this.bindtel = bindtel;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isBindwx() { return bindwx; }
    public void setBindwx(boolean bindwx) { this.bindwx = bindwx; }

    public boolean isBindtel() { return bindtel; }
    public void setBindtel(boolean bindtel) { this.bindtel = bindtel; }
}
