package com.guard.admin.payload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

@Getter
@Setter
public class Appointment {
    private String text;
    private String description;
    private Date startDate;
    private Date endDate;
    private Integer guardId;
    private Integer hours;
    private Double frequency;
    private String announces;
}
