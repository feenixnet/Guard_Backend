package com.guard.admin.payload.response;

import com.guard.admin.database.entities.Client;
import com.guard.admin.database.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtClientResponse {
  private String access_token;
  private String refresh_token;
  private Client client;

  public JwtClientResponse(String accessToken, String refreshToken, Client client) {
    this.access_token = accessToken;
    this.refresh_token = refreshToken;
    this.client = client;
  }
}
