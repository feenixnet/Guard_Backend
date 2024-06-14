package com.guard.admin.payload.response;

import com.guard.admin.database.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtStaffResponse {
  private String access_token;
  private String refresh_token;
  private User user;

  public JwtStaffResponse(String accessToken,String refreshToken, User user) {
    this.access_token = accessToken;
    this.refresh_token = refreshToken;
    this.user = user;
  }
}
