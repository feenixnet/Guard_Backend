package com.guard.admin.database.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "guard_id")
    private Guard guard;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    private String nature;
    private String description;
    private Date timestamp;
    private String otherInvolved;
    private String policeCaseNumber;
    private String policeOfficerContactDetails;
    private String signature;

    private Boolean approved;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User approvedBy;
}
