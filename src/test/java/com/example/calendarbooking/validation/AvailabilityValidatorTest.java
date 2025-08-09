package com.example.calendarbooking.validation;

import com.example.calendarbooking.dto.AvailabilityRuleDTO;
import com.example.calendarbooking.exception.ValidationException;
import com.example.calendarbooking.model.AvailabilityRule;
import com.example.calendarbooking.util.SlotUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvailabilityValidatorTest {

    private AvailabilityValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AvailabilityValidator();
    }

    @Test
    void shouldPassForValidRule() {
        AvailabilityRuleDTO dto = new AvailabilityRuleDTO(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertDoesNotThrow(() -> validator.validate(dto, List.of()));
    }

    @Test
    void shouldFailWhenStartAfterEnd() {
        AvailabilityRuleDTO dto = new AvailabilityRuleDTO(LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(10, 0));
        assertThrows(ValidationException.class, () -> validator.validate(dto, List.of()));
    }

    @Test
    void shouldFailWhenNot60MinuteBlock() {
        AvailabilityRuleDTO dto = new AvailabilityRuleDTO(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 30));
        assertThrows(ValidationException.class, () -> validator.validate(dto, List.of()));
    }

    @Test
    void shouldFailWhenOverlappingSlotExists() {
        AvailabilityRuleDTO dto = new AvailabilityRuleDTO(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        AvailabilityRule existing = new AvailabilityRule(LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(13, 0));
        assertThrows(ValidationException.class, () -> validator.validate(dto, List.of(existing)));
    }

    @Test
    void shouldPassBookingSlotIfAvailable() {
        AvailabilityRule rule = new AvailabilityRule(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertDoesNotThrow(() -> validator.validateBookingSlot(LocalDate.now(), LocalTime.of(10, 0), List.of(rule)));
    }

    @Test
    void shouldFailBookingSlotIfNotAvailable() {
        AvailabilityRule rule = new AvailabilityRule(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertThrows(ValidationException.class, () -> validator.validateBookingSlot(LocalDate.now(), LocalTime.of(9, 0), List.of(rule)));
    }
}