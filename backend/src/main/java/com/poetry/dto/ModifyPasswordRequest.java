package com.poetry.dto;

import jakarta.validation.constraints.NotBlank;

public class ModifyPasswordRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String passwordOld;

    @NotBlank
    private String passwordNew;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordOld() { return passwordOld; }
    public void setPasswordOld(String passwordOld) { this.passwordOld = passwordOld; }

    public String getPasswordNew() { return passwordNew; }
    public void setPasswordNew(String passwordNew) { this.passwordNew = passwordNew; }
}
