package com.example.calendarbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateSlotGroupDTO {
    private LocalDate date;
    private List<TimeSlotDTO> slots;
}