package com.guard.admin.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@Getter
@Setter
public class GuardRequest {
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String gender;
    private String phone;
    private String type;
    private String sin;
    private String role;
    private String transportation;
    private Date expiryDateForDriverLicense;
    private Date expiryDateForSecurityLicense;
    private Date expiryDateForFirearmsLicense;
    private Date birthday;
    private Date dateHired;
    private Integer systemPenalties;
    private Boolean status;
    private String firstAddress;
    private String secondAddress;
    private String city;
    private String province;
    private String postal;
    private String country;

    private MultipartFile driverImage;
    private MultipartFile securityImage;
    private MultipartFile firearmsImage;
}
