package com.example.calendarbooking.service;

import com.example.calendarbooking.dto.*;
import com.example.calendarbooking.model.Appointment;
import com.example.calendarbooking.model.AvailabilityRule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface BookingService {
    List<DateSlotGroupDTO> setAvailability(List<AvailabilityRuleDTO> dtos);

    List<TimeSlotDTO> getAvailableSlots(LocalDate date);

    AppointmentDTO bookAppointment(AppointmentRequestDTO dto);

    List<Appointment> getAllAppointments();
}