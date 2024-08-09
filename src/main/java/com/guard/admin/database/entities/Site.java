package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "sites")
public class Site {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;
    private String name;
    private String type;
    private String industry;
    private String address;
    private Date startDate;
    private double lat;
    private double lng;
    private String rule;
    private Integer radius;
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    
}