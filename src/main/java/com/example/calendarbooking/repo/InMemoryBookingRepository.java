package com.example.calendarbooking.repo;

import com.example.calendarbooking.model.Appointment;
import com.example.calendarbooking.model.AvailabilityRule;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final List<AvailabilityRule> availabilityRules = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();

    @Override
    public List<AvailabilityRule> getAvailabilityRulesForDate(LocalDate date) {
        return availabilityRules.stream()
                .filter(rule -> rule.getDate().equals(date))
                .sorted(Comparator.comparing(AvailabilityRule::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityRule> getAvailabilityRules() {
        return availabilityRules;
    }


    @Override
    public void addRule(AvailabilityRule rule) {
        availabilityRules.add(rule);
    }


    @Override
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public List<Appointment> getAppointments() {
        return appointments;
    }
}