package com.guard.admin.payload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class Appointment {
    private Integer id;
    private String text;
    private String description;
    private Date startDate;
    private Date endDate;
    private Integer guardId;
    private Double hours;
    private Double frequency;
    private String announces;
}
