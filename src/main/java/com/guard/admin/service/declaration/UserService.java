package com.guard.admin.service.declaration;

import java.util.List;

import com.google.firebase.auth.FirebaseAuthException;
import com.guard.admin.database.entities.*;
import com.guard.admin.payload.dto.ChatUser;
import com.guard.admin.payload.response.DataTableResponse;

public interface UserService {

    User getByEmail(String email);

    User get(Integer id);

    User create(User user) throws FirebaseAuthException;

    User update(Integer id, User userDetail) throws FirebaseAuthException;
    
    void delete(Integer id) throws FirebaseAuthException;

    List<User> getAreaManagers();

    List<User> getAll();

    List<ChatUser> getChatUsersForStaff(Integer id) ;

    List<ChatUser> getChatUsersForGuards(Integer guardId);

    List<ChatUser> getChatUsersForClients(Integer clientId);

    List<ChatUser> getChatUsersWithRole(Integer id, String role);

    List<ChatUser> getChatUsersForGuardsWithRole(Integer guardId, String role);

    List<ChatUser> getChatUsersForClientsWithRole(Integer clientId, String role);

    DataTableResponse<User> getPage(Integer pageNumber, Integer pageSize, String search);
}
