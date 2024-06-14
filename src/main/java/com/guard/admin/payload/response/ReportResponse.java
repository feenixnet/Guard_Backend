package com.guard.admin.payload.response;

import com.guard.admin.database.entities.Photo;
import com.guard.admin.database.entities.Report;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportResponse {
    Report report;
    List<Photo> photoList;
}
