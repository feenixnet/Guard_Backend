package com.guard.admin.database.entities;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "bugs")
public class Bug {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "guard_id")
    private Guard guard;
    
    private Date timestamp;
    private String description;
}
