package com.guard.admin.database.entities;

import java.sql.Date;

import com.guard.admin.payload.request.GuardRequest;
import com.guard.admin.utils.constant.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "guards")
public class Guard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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
    private String driverLicenseUrl;
    private Date expiryDateForDriverLicense;
    private String securityLicenseUrl;
    private Date expiryDateForSecurityLicense;
    private String firearmsLicenseUrl;
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

    public Guard() {
        this.type = "Full";
        this.role = Role.guard;
        this.status = false;
    }

    public Guard( String email2, String encode, String firstname2, String lastname2, String gender, String role) {
        this.email = email2;
        this.password = encode;
        this.firstname = firstname2;
        this.lastname = lastname2;
        this.gender = gender;
        this.role = Role.guard;
    }

    public Guard(GuardRequest request) {
        this.email = request.getEmail();
        this.firstname = request.getFirstname();
        this.lastname = request.getLastname();
        this.gender = request.getGender();
        this.phone = request.getPhone();
        this.type = request.getType();
        this.sin = request.getSin();
        this.transportation = request.getTransportation();
        this.birthday = request.getBirthday();
        this.dateHired = request.getDateHired();
        this.systemPenalties = request.getSystemPenalties();
        this.status = request.getStatus();
        this.firstAddress = request.getFirstAddress();
        this.secondAddress = request.getSecondAddress();
        this.city = request.getCity();
        this.province = request.getProvince();
        this.postal = request.getPostal();
        this.country = request.getCountry();
    }

}
