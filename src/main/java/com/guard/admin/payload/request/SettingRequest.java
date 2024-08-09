package com.guard.admin.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@Getter
@Setter
public class SettingRequest {
    
    private Integer id;
    private String timezone;
    private String company_name;
    private String company_address;
    private String company_email;
    private String company_phone;
    private String company_logo;
    private MultipartFile company_logo_file;
}
