package com.guard.admin.payload.request;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VisitorRequest {
    private Integer site_id;
    private String fullname;
    private String company;
    private String phonenumber;
    private String licenseplate;
    private String reason;
    private MultipartFile image;
    private Date timestamp;
}
