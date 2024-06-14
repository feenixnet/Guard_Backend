package com.guard.admin.service.declaration;

import java.io.IOException;
import java.util.*;

import com.guard.admin.database.entities.*;
import com.guard.admin.payload.request.GuardRequest;
import com.guard.admin.payload.response.DataTableResponse;

public interface GuardService {

    Guard get(Integer id);

    Guard create(GuardRequest guardRequest) throws IOException;

    Guard update(Integer id , GuardRequest request) throws IOException;

    void delete(Integer id);

    Guard analyzeRequest(GuardRequest guardRequest) throws IOException;

    List<Guard> getAll();

    List<Guard> getByUserId(Integer id);

    List<Guard> getBySiteId(Integer id);

    List<Guard> getByType(String type);

    DataTableResponse<Guard> getPage(Integer pageNum, Integer pageLength, String search);
}
