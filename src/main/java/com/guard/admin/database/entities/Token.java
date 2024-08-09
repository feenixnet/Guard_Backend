package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;
    private Integer userId;
    private String role;
    private String token;
}
