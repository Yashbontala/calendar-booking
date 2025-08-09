package com.example.calendarbooking.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private String inviteeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
