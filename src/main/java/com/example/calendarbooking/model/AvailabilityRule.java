package com.example.calendarbooking.model;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityRule {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}