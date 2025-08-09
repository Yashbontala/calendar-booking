package com.example.calendarbooking.repo;

import com.example.calendarbooking.model.Appointment;
import com.example.calendarbooking.model.AvailabilityRule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookingRepository {
    List<AvailabilityRule> getAvailabilityRulesForDate(LocalDate date);

    void addRule(AvailabilityRule rule);

    List<AvailabilityRule> getAvailabilityRules();

    void addAppointment(Appointment appointment);

    List<Appointment> getAppointments();
}
