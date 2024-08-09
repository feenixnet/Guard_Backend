package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    private String name;
    private String make;
    private String model;
    private String year;
    private String license_number;

}