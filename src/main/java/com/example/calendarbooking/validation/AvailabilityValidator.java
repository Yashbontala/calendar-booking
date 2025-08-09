package com.example.calendarbooking.validation;

import com.example.calendarbooking.dto.AvailabilityRuleDTO;
import com.example.calendarbooking.dto.TimeSlotDTO;
import com.example.calendarbooking.exception.ValidationException;
import com.example.calendarbooking.model.AvailabilityRule;
import com.example.calendarbooking.util.SlotUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class AvailabilityValidator {

    public void validate(AvailabilityRuleDTO dto, List<AvailabilityRule> existingForDate) {
        LocalDate date = dto.getDate();
        LocalTime start = dto.getStartTime();
        LocalTime end = dto.getEndTime();

        //  Basic null checks
        if (date == null || start == null || end == null) {
            throw new ValidationException("Date, start time, and end time must not be null.");
        }

        //  Start < End check
        if (!start.isBefore(end)) {
            throw new ValidationException("Start time must be before end time for date: " + date);
        }

        //  Must be multiple of 60 minutes
        Duration duration = Duration.between(start, end);
        if (duration.toMinutes() % 60 != 0) {
            throw new ValidationException("Availability window on " + date + " must be in 60-minute blocks.");
        }

        //  Generate slots for the new rule
        List<TimeSlotDTO> newSlots = SlotUtils.generateSlots(start, end);

        //  Check overlap at slot level
        for (AvailabilityRule existing : existingForDate) {
            List<TimeSlotDTO> existingSlots = SlotUtils.generateSlots(existing.getStartTime(), existing.getEndTime());

            for (TimeSlotDTO slot : newSlots) {
                boolean clash = existingSlots.stream().anyMatch(es -> es.getStartTime().equals(slot.getStartTime()));
                if (clash) {
                    throw new ValidationException("Overlapping availability detected for date: " + date + " at " + slot.getStartTime());
                }
            }
        }
    }

    public void validateBookingSlot(LocalDate date, LocalTime start, List<AvailabilityRule> availabilityRules) {
        // Check if slot exists in availability
        List<TimeSlotDTO> availableSlots = availabilityRules.stream().filter(r -> r.getDate().equals(date)).flatMap(r -> SlotUtils.generateSlots(r.getStartTime(), r.getEndTime()).stream()).toList();

        boolean exists = availableSlots.stream().anyMatch(slot -> slot.getStartTime().equals(start));

        if (!exists) {
            throw new ValidationException("Selected slot " + start + " on " + date + " is not available.");
        }
    }
}
