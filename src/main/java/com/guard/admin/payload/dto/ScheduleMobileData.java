package com.guard.admin.payload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScheduleMobileData {
    private Integer carId;
    private List<Appointment> appointmentList;
}
