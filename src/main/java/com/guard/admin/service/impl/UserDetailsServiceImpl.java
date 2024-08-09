package com.guard.admin.service.impl;

import com.guard.admin.database.entities.Client;
import com.guard.admin.database.entities.Guard;
import com.guard.admin.database.repositories.ClientRepository;
import com.guard.admin.database.repositories.GuardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guard.admin.database.entities.User;
import com.guard.admin.database.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GuardRepository guardRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    if (userRepository.existsByEmail(email)) {
      User existingUser = userRepository.findByEmail(email).get();
      return UserDetailsImpl.build(existingUser);
    }
    else if (guardRepository.existsByEmail(email)) {
      Guard existingGuard = guardRepository.findByEmail(email).get();
      return UserDetailsImpl.build(existingGuard);
    }
    else if (clientRepository.existsByEmail(email)) {
      Client existingClient = clientRepository.findByEmail(email).get();
      return UserDetailsImpl.build(existingClient);
    }

    throw new UsernameNotFoundException("User not found with email: " + email);
  }

}
