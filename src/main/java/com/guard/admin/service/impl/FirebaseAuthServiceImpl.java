package com.guard.admin.service.impl;

import com.guard.admin.service.declaration.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.database.repositories.ClientRepository;
import com.guard.admin.database.repositories.UserRepository;

@Service
public class FirebaseAuthServiceImpl implements FirebaseAuthService {

    @Autowired
    private GuardRepository guardRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserRecord createUser(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
            .setEmail(email)
            .setPassword(password);

        return FirebaseAuth.getInstance().createUser(request);
    }
    @Override
    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
    @Override
    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().getUserByEmail(email);
    }
    @Override
    public void deleteUser(String email) throws FirebaseAuthException {
        FirebaseAuth.getInstance().deleteUser(FirebaseAuth.getInstance().getUserByEmail(email).getUid());
    }
    @Override
    public UserRecord updateGuardUser(Integer id, String email, String password) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(FirebaseAuth.getInstance().getUserByEmail(guardRepository.findById(id).get().getEmail()).getUid())
            .setEmail(email);
        if(!password.isEmpty())
            request.setPassword(password);
        
        return FirebaseAuth.getInstance().updateUser(request);
    }

    @Override
    public UserRecord updateClientUser(Integer id, String email, String password) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(FirebaseAuth.getInstance().getUserByEmail(clientRepository.findById(id).get().getEmail()).getUid())
            .setEmail(email);
        if(!password.isEmpty())
            request.setPassword(password);
        
        return FirebaseAuth.getInstance().updateUser(request);
    }

    @Override
    public UserRecord updateStaffUser(Integer id, String email, String password) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(FirebaseAuth.getInstance().getUserByEmail(userRepository.findById(id).get().getEmail()).getUid())
            .setEmail(email);
        if(!password.isEmpty())
            request.setPassword(password);
        
        return FirebaseAuth.getInstance().updateUser(request);
    }

    // Additional methods for updating and deleting users can be added here
}