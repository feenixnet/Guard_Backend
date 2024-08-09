package com.guard.admin.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table
public class Event {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer guardId;
    private Integer siteId;
    private Integer hitPointId;
    private Integer scheduleId;
    private String action;
    private String description;
    private Date timestamp;
}
