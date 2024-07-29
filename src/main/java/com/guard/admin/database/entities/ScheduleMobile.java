package com.guard.admin.database.entities;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "schedule_mobile")
public class ScheduleMobile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "guard_id")
    private Guard guard;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    private Date startTime;
    private Date endTime;
    private Integer hours;
    private Double frequency;
    private String announces;
}