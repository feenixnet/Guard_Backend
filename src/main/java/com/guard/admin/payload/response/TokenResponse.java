package com.guard.admin.payload.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenResponse {

    @NotBlank
    private String token;

    public TokenResponse(String newAccessToken)
    {
        this.token = newAccessToken;
    }
}