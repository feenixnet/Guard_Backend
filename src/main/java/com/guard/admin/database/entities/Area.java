package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    private String name;
    private String siteIds;
    private Integer carId;

}
