package com.guard.admin.payload.response;

import com.guard.admin.database.entities.Visitor;
import lombok.Data;

@Data
public class VisitorResponse {
    private Visitor visitor;
}
