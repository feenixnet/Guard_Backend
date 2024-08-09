package com.guard.admin.service.declaration;

import java.util.List;

import com.guard.admin.payload.response.DataTableResponse;
import com.google.firebase.auth.FirebaseAuthException;
import com.guard.admin.database.entities.Client;

public interface ClientService {

    Client get(Integer id);
    
    Client create(Client client) throws FirebaseAuthException;

    void update(Integer id , Client clientDetail) throws FirebaseAuthException;

    void delete(Integer id) throws FirebaseAuthException;

    List<Client> getAll();

    DataTableResponse<Client> getPage(Integer pageNumber, Integer pageSize, String searchKeyword);
}
