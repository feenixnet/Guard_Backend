package com.guard.admin.service.declaration;

import java.io.IOException;
import java.util.*;

import com.google.firebase.auth.FirebaseAuthException;
import com.guard.admin.database.entities.*;
import com.guard.admin.payload.request.GuardRequest;
import com.guard.admin.payload.response.DataTableResponse;

public interface GuardService {

    Guard get(Integer id);

    Guard create(GuardRequest guardRequest) throws IOException, FirebaseAuthException;

    Guard update(Integer id , GuardRequest request) throws IOException, FirebaseAuthException;

    void delete(Integer id) throws FirebaseAuthException;

    Guard analyzeRequest(GuardRequest guardRequest) throws IOException;

    List<Guard> getAll();

    List<Guard> getByUserId(Integer id);

    List<Guard> getBySiteId(Integer id);

    List<Guard> getByType(String type);

    DataTableResponse<Guard> getPage(Integer pageNum, Integer pageLength, String search);
}
