package com.guard.admin.service.declaration;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public interface FirebaseAuthService {

    public UserRecord createUser(String email, String password) throws FirebaseAuthException;

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException;

    public UserRecord getUserByEmail(String email) throws FirebaseAuthException;

    public void deleteUser(String uid) throws FirebaseAuthException;

    public UserRecord updateGuardUser(Integer id, String email, String password) throws FirebaseAuthException;

    public UserRecord updateClientUser(Integer id, String email, String password) throws FirebaseAuthException;

    public UserRecord updateStaffUser(Integer id, String email, String password) throws FirebaseAuthException;

    // Additional methods for updating and deleting users can be added here
}