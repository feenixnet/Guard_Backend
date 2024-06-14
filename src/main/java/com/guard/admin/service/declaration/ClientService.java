package com.guard.admin.service.declaration;

import java.util.List;

import com.guard.admin.payload.response.DataTableResponse;
import com.guard.admin.database.entities.Client;

public interface ClientService {

    Client get(Integer id);
    
    Client create(Client client);

    void update(Integer id , Client clientDetail);

    void delete(Integer id);

    List<Client> getAll();

    DataTableResponse<Client> getPage(Integer pageNumber, Integer pageSize, String searchKeyword);
}
