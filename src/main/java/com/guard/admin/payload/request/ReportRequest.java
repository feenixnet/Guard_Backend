package com.guard.admin.payload.request;


import com.guard.admin.database.entities.Photo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {
    private Photo[] photos;
    private Integer siteId;
    private String nature;
    private String description;
    private String otherInvolved;
    private String policeCaseNumber;
    private String policeOfficerContactDetails;
    private String signature;
}
