package com.guard.admin.service.impl;

import com.guard.admin.database.repositories.ClientRepository;
import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.database.repositories.UserRepository;
import com.guard.admin.service.declaration.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuardRepository guardRepository;

    @Override
    public UserDetailsImpl getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (UserDetailsImpl) authentication.getPrincipal();
    }

    @Override
    public boolean isDuplicate(String email) {
        if(clientRepository.existsByEmail(email)) return true;
        if(guardRepository.existsByEmail(email)) return true;
        return !userRepository.existsByEmail(email);
    }
}
