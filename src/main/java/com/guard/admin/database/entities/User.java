package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;


@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private String email;
    private String password;
    private String role;
    private String firstname;
    private String lastname;
    private String address;
    private String gender;
    private Date birthday;
    private String phone;

    public User() {

    }

    public User(String email, String password, String role, String firstname, String lastname) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}