package com.guard.admin.database.entities;

import com.guard.admin.utils.constant.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private String password;
    private String address;
    private String phone;
    private String email;
    private String position;
    private String company;
    private String role;
    private String gender;

    public Client() {
        this.role = Role.client;
    }

    public Client( String email2, String encode, String firstname2, String lastname2, String gender, String role) {
        this.email = email2;
        this.password = encode;
        this.firstname = firstname2;
        this.lastname = lastname2;
        this.role = Role.client;
    }

}