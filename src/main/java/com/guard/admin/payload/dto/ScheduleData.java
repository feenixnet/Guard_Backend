package com.guard.admin.payload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScheduleData {
    private Integer siteId;
    private List<Appointment> appointmentList;
}
