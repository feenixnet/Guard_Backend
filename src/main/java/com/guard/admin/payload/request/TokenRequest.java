package com.guard.admin.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenRequest {

  @NotBlank
  private String token;
}
