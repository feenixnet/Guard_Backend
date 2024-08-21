package com.guard.admin.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.guard.admin.database.entities.Bug;
import com.guard.admin.database.repositories.BugRepository;
import com.guard.admin.database.repositories.ClientRepository;
import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.database.repositories.UserRepository;


import com.guard.admin.service.declaration.BugService;
import com.guard.admin.utils.constant.Role;

public class BugServiceImpl implements BugService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    GuardRepository guardRepository;

    @Autowired
    BugRepository bugRepository;

    @Override
    public Boolean create(UserDetailsImpl userDetails, String description)
    {
        try{
            Bug bug = new Bug();
            bug.setTimestamp(new Date());
            bug.setDescription(description);

            if(userDetails.getRole() == Role.client) {
                bug.setClient(clientRepository.findById(userDetails.getId()).get());
            } else if(userDetails.getRole() == Role.guard){
                bug.setGuard(guardRepository.findById(userDetails.getId()).get());
            } else {
                bug.setUser(userRepository.findById(userDetails.getId()).get());
            }
            
            bugRepository.save(bug);
            return true;
        } catch (Exception e) {
            return false;
        }
       
        
    }
}
