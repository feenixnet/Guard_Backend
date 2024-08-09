package com.guard.admin.service.impl;

import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.guard.admin.database.entities.Client;
import com.guard.admin.database.entities.Guard;
import com.guard.admin.utils.constant.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.guard.admin.database.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Integer id;

  private String username;

  private String email;

  @JsonIgnore
  private String password;

  @JsonIgnore
  private String role;

  @JsonIgnore
  private String firstname;

  @JsonIgnore
  private String lastname;

  @JsonIgnore
  private String address;

  @JsonIgnore
  private String gender;

  @JsonIgnore
  private Date birthday;

  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(Integer string, String username, String email, String password,String role,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = string;
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
    this.authorities = authorities;
  }

  public static UserDetailsImpl build(User user) {

    List<GrantedAuthority> authorities = Collections.singletonList(
      new SimpleGrantedAuthority(user.getRole())
    );
    

    return new UserDetailsImpl(
        user.getId(),
        user.getEmail(),
        user.getEmail(),
        user.getPassword(),
        user.getRole(),
        authorities);
  }

  public static UserDetailsImpl build(Guard guard) {

    List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(Role.guard)
    );

    return new UserDetailsImpl(
            guard.getId(),
            guard.getEmail(),
            guard.getEmail(),
            guard.getPassword(),
            Role.guard,
            authorities
    );
  }

  public static UserDetailsImpl build(Client client) {

    List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(Role.client)
    );

    return new UserDetailsImpl(
            client.getId(),
            client.getEmail(),
            client.getEmail(),
            client.getPassword(),
            Role.client,
            authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Integer getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public String getRole() {
    return role;
  }

  public String getFirstName() {
    return firstname;
  }

  public String getLastName() {
    return lastname;
  }

  public String getAddress() {
    return address;
  }

  public String getGender() {
    return gender;
  }

  public Date getBirthday() {
    return birthday;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
