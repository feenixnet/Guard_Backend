package com.guard.admin.payload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;

@Getter
@Setter
public class Appointment {
    private Integer id;
    private String text;
    private String description;
    private Date startDate;
    private Date endDate;
    private Integer guardId;
    private Integer hours;
    private Double frequency;
    private String announces;
}
