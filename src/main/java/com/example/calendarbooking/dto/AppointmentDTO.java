package com.example.calendarbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AppointmentDTO {
    private String inviteeName;
    private LocalDate date;
    private TimeSlotDTO slot;
}
