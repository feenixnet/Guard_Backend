package com.guard.admin.payload.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {
    private Integer[] photoIds;
    private Integer siteId;
    private String nature;
    private String description;
    private String otherInvolved;
    private String policeCaseNumber;
    private String policeOfficerContactDetails;
    private String signature;
}
