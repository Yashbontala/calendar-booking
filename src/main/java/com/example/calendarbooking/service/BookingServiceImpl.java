package com.example.calendarbooking.service;

import com.example.calendarbooking.dto.*;
import com.example.calendarbooking.exception.ValidationException;
import com.example.calendarbooking.model.Appointment;
import com.example.calendarbooking.model.AvailabilityRule;
import com.example.calendarbooking.repo.BookingRepository;
import com.example.calendarbooking.util.SlotUtils;
import com.example.calendarbooking.validation.AvailabilityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository repo;

    @Autowired
    private AvailabilityValidator validator;

    @Override
    public List<DateSlotGroupDTO> setAvailability(List<AvailabilityRuleDTO> dtos) {
        Set<LocalDate> dates = dtos.stream().map(AvailabilityRuleDTO::getDate).collect(Collectors.toSet());

        List<AvailabilityRule> currentRules = repo.getAvailabilityRules().stream().filter(rule -> dates.contains(rule.getDate())).collect(Collectors.toList());

        List<AvailabilityRule> newRules = new ArrayList<>();
        List<DateSlotGroupDTO> result = new ArrayList<>();

        for (AvailabilityRuleDTO dto : dtos) {
            List<AvailabilityRule> existingForDate = currentRules.stream().filter(r -> r.getDate().equals(dto.getDate())).toList();

            //  Single point of validation
            validator.validate(dto, existingForDate);

            AvailabilityRule rule = new AvailabilityRule(dto.getDate(), dto.getStartTime(), dto.getEndTime());
            newRules.add(rule);

            //  Slot generation via utility
            List<TimeSlotDTO> slots = SlotUtils.generateSlots(dto.getStartTime(), dto.getEndTime());
            result.add(new DateSlotGroupDTO(dto.getDate(), slots));
        }

        newRules.forEach(repo::addRule);

        return result;
    }


    @Override
    public List<TimeSlotDTO> getAvailableSlots(LocalDate date) {
        List<AvailabilityRule> rules = repo.getAvailabilityRulesForDate(date);

        List<TimeSlotDTO> slots = new ArrayList<>();
        for (AvailabilityRule rule : rules) {
            LocalTime time = rule.getStartTime();
            while (time.plusHours(1).isBefore(rule.getEndTime().plusSeconds(1))) {
                LocalDateTime slotStart = LocalDateTime.of(date, time);
                boolean booked = repo.getAppointments().stream().anyMatch(a -> a.getStartTime().equals(slotStart));
                if (!booked) {
                    slots.add(new TimeSlotDTO(time, time.plusHours(1)));
                }
                time = time.plusHours(1);
            }
        }
        return slots;
    }

    @Override
    public AppointmentDTO bookAppointment(AppointmentRequestDTO dto) {
        //  Check if requested slot exists in availability
        validator.validateBookingSlot(dto.getDate(), dto.getStartTime(), repo.getAvailabilityRules());

        LocalDateTime start = LocalDateTime.of(dto.getDate(), dto.getStartTime());
        LocalDateTime end = start.plusHours(1);

        //  Check if already booked
        boolean exists = repo.getAppointments().stream().anyMatch(a -> a.getStartTime().equals(start));
        if (exists) {
            throw new ValidationException("Slot already booked");
        }

        // Save appointment
        repo.addAppointment(new Appointment(dto.getInviteeName(), start, end));

        return new AppointmentDTO(dto.getInviteeName(), dto.getDate(), new TimeSlotDTO(dto.getStartTime(), end.toLocalTime()));
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return repo.getAppointments();
    }
}