package com.guard.admin.payload.response;

import com.guard.admin.database.entities.Guard;
import com.guard.admin.database.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtGuardResponse {
  private String access_token;
  private String refresh_token;
  private Guard guard;

  public JwtGuardResponse(String accessToken, String refreshToken, Guard guard) {
    this.access_token = accessToken;
    this.refresh_token = refreshToken;
    this.guard = guard;
  }
}
