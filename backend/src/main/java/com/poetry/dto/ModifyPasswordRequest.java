package com.poetry.dto;

import com.poetry.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public class ModifyPasswordRequest {

    @NotBlank
    private String passwordOld;

    @NotBlank
    @StrongPassword
    private String passwordNew;

    public String getPasswordOld() { return passwordOld; }
    public void setPasswordOld(String passwordOld) { this.passwordOld = passwordOld; }

    public String getPasswordNew() { return passwordNew; }
    public void setPasswordNew(String passwordNew) { this.passwordNew = passwordNew; }
}
