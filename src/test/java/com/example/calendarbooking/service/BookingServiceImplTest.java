package com.example.calendarbooking.service;

import com.example.calendarbooking.dto.*;
import com.example.calendarbooking.exception.ValidationException;
import com.example.calendarbooking.model.Appointment;
import com.example.calendarbooking.model.AvailabilityRule;
import com.example.calendarbooking.repo.BookingRepository;
import com.example.calendarbooking.validation.AvailabilityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private BookingRepository repo;

    @Mock
    private AvailabilityValidator validator;

    @InjectMocks
    private BookingServiceImpl service;

    private LocalDate today;
    private AvailabilityRuleDTO ruleDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        today = LocalDate.of(2025, 8, 10);
        ruleDTO = new AvailabilityRuleDTO(today, LocalTime.of(10, 0), LocalTime.of(12, 0));
    }

    @Test
    void shouldSetAvailabilitySuccessfully() {
        when(repo.getAvailabilityRules()).thenReturn(new ArrayList<>());

        List<DateSlotGroupDTO> result = service.setAvailability(List.of(ruleDTO));

        assertEquals(1, result.size());
        assertEquals(today, result.get(0).getDate());
        assertEquals(2, result.get(0).getSlots().size());
        verify(validator, times(1)).validate(eq(ruleDTO), anyList());
        verify(repo, times(1)).addRule(any(AvailabilityRule.class));
    }

    @Test
    void shouldFailSetAvailabilityWhenValidationFails() {
        doThrow(new ValidationException("Invalid")).when(validator).validate(eq(ruleDTO), anyList());
        when(repo.getAvailabilityRules()).thenReturn(new ArrayList<>());

        assertThrows(ValidationException.class, () -> service.setAvailability(List.of(ruleDTO)));
        verify(repo, never()).addRule(any());
    }

    @Test
    void shouldGetAvailableSlotsWhenNoBookings() {
        AvailabilityRule rule = new AvailabilityRule(today, LocalTime.of(9, 0), LocalTime.of(11, 0));
        when(repo.getAvailabilityRulesForDate(today)).thenReturn(List.of(rule));
        when(repo.getAppointments()).thenReturn(new ArrayList<>());

        List<TimeSlotDTO> slots = service.getAvailableSlots(today);

        assertEquals(2, slots.size());
        assertEquals(LocalTime.of(9, 0), slots.get(0).getStartTime());
    }

    @Test
    void shouldGetAvailableSlotsWhenSomeBooked() {
        AvailabilityRule rule = new AvailabilityRule(today, LocalTime.of(9, 0), LocalTime.of(11, 0));
        Appointment booked = new Appointment("John", LocalDateTime.of(today, LocalTime.of(9, 0)), LocalDateTime.of(today, LocalTime.of(10, 0)));

        when(repo.getAvailabilityRulesForDate(today)).thenReturn(List.of(rule));
        when(repo.getAppointments()).thenReturn(List.of(booked));

        List<TimeSlotDTO> slots = service.getAvailableSlots(today);

        assertEquals(1, slots.size());
        assertEquals(LocalTime.of(10, 0), slots.get(0).getStartTime());
    }

    @Test
    void shouldBookAppointmentSuccessfully() {
        AppointmentRequestDTO request = new AppointmentRequestDTO("John", today, LocalTime.of(10, 0));

        doNothing().when(validator).validateBookingSlot(eq(today), eq(LocalTime.of(10, 0)), anyList());
        when(repo.getAppointments()).thenReturn(new ArrayList<>());
        when(repo.getAvailabilityRules()).thenReturn(List.of(new AvailabilityRule(today, LocalTime.of(10, 0), LocalTime.of(12, 0))));

        AppointmentDTO result = service.bookAppointment(request);

        assertEquals("John", result.getInviteeName());
        assertEquals(today, result.getDate());
        assertEquals(LocalTime.of(10, 0), result.getSlot().getStartTime());
        verify(repo, times(1)).addAppointment(any(Appointment.class));
    }

    @Test
    void shouldFailBookingIfSlotNotAvailable() {
        AppointmentRequestDTO request = new AppointmentRequestDTO("John", today, LocalTime.of(10, 0));
        doThrow(new ValidationException("Not available")).when(validator).validateBookingSlot(eq(today), eq(LocalTime.of(10, 0)), anyList());

        when(repo.getAvailabilityRules()).thenReturn(new ArrayList<>());

        assertThrows(ValidationException.class, () -> service.bookAppointment(request));
        verify(repo, never()).addAppointment(any());
    }

    @Test
    void shouldFailBookingIfSlotAlreadyBooked() {
        AppointmentRequestDTO request = new AppointmentRequestDTO("John", today, LocalTime.of(10, 0));

        doNothing().when(validator).validateBookingSlot(eq(today), eq(LocalTime.of(10, 0)), anyList());
        Appointment booked = new Appointment("Jane", LocalDateTime.of(today, LocalTime.of(10, 0)), LocalDateTime.of(today, LocalTime.of(11, 0)));
        when(repo.getAppointments()).thenReturn(List.of(booked));
        when(repo.getAvailabilityRules()).thenReturn(List.of(new AvailabilityRule(today, LocalTime.of(10, 0), LocalTime.of(12, 0))));

        assertThrows(ValidationException.class, () -> service.bookAppointment(request));
        verify(repo, never()).addAppointment(any());
    }

    @Test
    void shouldReturnAllAppointments() {
        List<Appointment> appointments = List.of(new Appointment("John", LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        when(repo.getAppointments()).thenReturn(appointments);

        List<Appointment> result = service.getAllAppointments();

        assertEquals(appointments, result);
    }
}
