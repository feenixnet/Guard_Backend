package com.guard.admin.database.entities;

import com.guard.admin.utils.constant.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "staffs")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
