package com.guard.admin.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table
public class Photo {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer reportId;
    private String url;
    private Date timestamp;
}
