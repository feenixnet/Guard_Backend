package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "timezone")
public class TimeZone {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;
    private String zone;
}
