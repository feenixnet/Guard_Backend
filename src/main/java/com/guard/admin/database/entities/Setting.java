package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "setting")
public class Setting {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;
    private String timezone;
    private String company_name;
    private String company_address;
    private String company_email;
    private String company_phone;
    private String company_logo;
}
