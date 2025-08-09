package com.example.calendarbooking.util;

import com.example.calendarbooking.dto.TimeSlotDTO;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class SlotUtils {

    // Generate all 1-hour slots between start and end
    public static List<TimeSlotDTO> generateSlots(LocalTime start, LocalTime end) {
        List<TimeSlotDTO> slots = new ArrayList<>();
        LocalTime current = start;

        while (current.plusHours(1).isBefore(end.plusSeconds(1))) {
            slots.add(new TimeSlotDTO(current, current.plusHours(1)));
            current = current.plusHours(1);
        }
        return slots;
    }
}