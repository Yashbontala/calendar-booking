package com.example.calendarbooking.controller;

import com.example.calendarbooking.dto.TimeSlotDTO;
import com.example.calendarbooking.exception.GlobalExceptionHandler;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SlotControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private CalendarController slotController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(slotController).setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        today = LocalDate.of(2025, 8, 9);
    }

    @Test
    void shouldReturnAvailableSlots() throws Exception {
        List<TimeSlotDTO> slots = List.of(new TimeSlotDTO(LocalTime.of(10, 0), LocalTime.of(11, 0)), new TimeSlotDTO(LocalTime.of(11, 0), LocalTime.of(12, 0)));

        when(bookingService.getAvailableSlots(today)).thenReturn(slots);

        mockMvc.perform(get("/api/calendar/slots").param("date", today.toString()).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Available slots fetched")).andExpect(jsonPath("$.data").isArray()).andExpect(jsonPath("$.data[0].startTime").value("10:00:00")).andExpect(jsonPath("$.data[0].endTime").value("11:00:00"));
    }

    @Test
    void shouldReturnEmptySlotsWhenNoneAvailable() throws Exception {
        when(bookingService.getAvailableSlots(today)).thenReturn(List.of());

        mockMvc.perform(get("/api/calendar/slots").param("date", today.toString()).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.data").isEmpty());
    }
}