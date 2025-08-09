package com.example.calendarbooking.controller;

import com.example.calendarbooking.dto.AvailabilityRuleDTO;
import com.example.calendarbooking.dto.DateSlotGroupDTO;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private CalendarController availabilityController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(availabilityController).setControllerAdvice(new GlobalExceptionHandler()).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        today = LocalDate.of(2025, 8, 9);
    }

    @Test
    void shouldSetAvailabilitySuccessfully() throws Exception {
        AvailabilityRuleDTO dto = new AvailabilityRuleDTO(today, LocalTime.of(10, 0), LocalTime.of(12, 0));
        DateSlotGroupDTO response = new DateSlotGroupDTO(today, List.of());

        when(bookingService.setAvailability(anyList())).thenReturn(List.of(response));

        mockMvc.perform(post("/api/calendar/availability").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(List.of(dto)))).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Availability set successfully"));

        verify(bookingService, times(1)).setAvailability(anyList());
    }

    @Test
    void shouldFailWhenAddingOverlappingAvailability() throws Exception {
        AvailabilityRuleDTO dto = new AvailabilityRuleDTO(today, LocalTime.of(10, 0), LocalTime.of(12, 0));

        when(bookingService.setAvailability(anyList())).thenThrow(new ValidationException("Overlapping availability exists for date: " + today));

        mockMvc.perform(post("/api/calendar/availability").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(List.of(dto)))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Overlapping availability exists for date: " + today));
    }
}