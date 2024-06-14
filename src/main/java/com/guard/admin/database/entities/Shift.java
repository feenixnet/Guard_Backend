package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private Integer siteId;
    private Integer day;
    private String startTime;
    private Integer hours;
    private Integer count;
    private Double frequency;
    private Boolean status;
}
