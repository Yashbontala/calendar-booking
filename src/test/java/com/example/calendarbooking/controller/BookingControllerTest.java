package com.example.calendarbooking.controller;

import com.example.calendarbooking.dto.AppointmentDTO;
import com.example.calendarbooking.dto.AppointmentRequestDTO;
import com.example.calendarbooking.dto.TimeSlotDTO;
import com.example.calendarbooking.exception.GlobalExceptionHandler;
import com.example.calendarbooking.exception.ValidationException;
import com.example.calendarbooking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private CalendarController bookingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        today = LocalDate.of(2025, 8, 9);
    }

    @Test
    void shouldBookAppointmentSuccessfully() throws Exception {
        AppointmentRequestDTO request = new AppointmentRequestDTO("John", today, LocalTime.of(10, 0));

        when(bookingService.bookAppointment(any())).thenReturn(new AppointmentDTO("John", today, new TimeSlotDTO(LocalTime.of(10, 0), LocalTime.of(11, 0))));

        mockMvc.perform(post("/api/calendar/book").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Appointment booked!"));
    }

    @Test
    void shouldFailWhenSlotAlreadyTaken() throws Exception {
        AppointmentRequestDTO request = new AppointmentRequestDTO("John", today, LocalTime.of(10, 0));

        when(bookingService.bookAppointment(any())).thenThrow(new ValidationException("Slot already booked"));

        mockMvc.perform(post("/api/calendar/book").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Slot already booked"));
    }
}
