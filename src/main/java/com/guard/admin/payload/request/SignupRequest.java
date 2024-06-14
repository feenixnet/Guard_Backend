package com.guard.admin.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequest {
  @NotBlank
  @Size(max = 50)
  private String firstname;

  @NotBlank
  @Size(max = 50)
  private String lastname;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private String role = "";

  private String gender = "";

  @NotBlank
  @Size(max = 40)
  private String password;

}
