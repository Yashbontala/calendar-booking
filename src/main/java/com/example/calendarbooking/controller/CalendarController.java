package com.example.calendarbooking.controller;

import com.example.calendarbooking.dto.*;
import com.example.calendarbooking.model.Appointment;
import com.example.calendarbooking.service.BookingService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@Tag(name = "Calendar Management", description = "APIs for managing calendar availability and appointments")


public class CalendarController {

    @Autowired
    private BookingService service;

    @Operation(summary = "Set availability for specific dates", description = "Creates availability slots for given date(s) and time range(s). " + "Availability must be in 60-minute blocks. Overlapping slots will cause a validation error.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of availability rules to set", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvailabilityRuleDTO.class), examples = {@ExampleObject(name = "Single Day Availability", summary = "Setting availability for one day", value = """
            [{
                "date": "2025-08-15",
                "startTime": "09:00",
                "endTime": "12:00"
            }]
            """), @ExampleObject(name = "Multiple Days Availability", summary = "Setting availability for multiple days", value = """
            [
                {
                    "date": "2025-08-15",
                    "startTime": "09:00",
                    "endTime": "12:00"
                },
                {
                    "date": "2025-08-15",
                    "startTime": "14:00",
                    "endTime": "17:00"
                },
                {
                    "date": "2025-08-16",
                    "startTime": "10:00",
                    "endTime": "16:00"
                }
            ]
            """)})), responses = {@ApiResponse(responseCode = "200", description = "Availability set successfully", content = @Content(schema = @Schema(implementation = DateSlotGroupDTO.class))), @ApiResponse(responseCode = "400", description = "Invalid availability (overlap, invalid times, missing data)", content = @Content(examples = @ExampleObject(value = "{ \"message\": \"Overlapping availability exists for date: 2025-08-15\" }")))})
    @PostMapping("/availability")
    public ResponseEntity<?> setAvailability(@RequestBody @Valid List<AvailabilityRuleDTO> ruleDTOs) {
        List<DateSlotGroupDTO> updatedSlots = service.setAvailability(ruleDTOs);
        return ResponseEntity.ok(new GeneralResponse<>("Availability set successfully", updatedSlots));
    }

    @Operation(summary = "Get available slots for a date", description = "Fetches all free 60-minute slots for a given date, excluding booked ones.", parameters = {@Parameter(name = "date", description = "Date to check availability for (yyyy-MM-dd)", required = true, example = "2025-08-15")}, responses = @ApiResponse(responseCode = "200", description = "Slots retrieved successfully", content = @Content(schema = @Schema(implementation = TimeSlotDTO.class))))
    @GetMapping("/slots")
    public ResponseEntity<?> getSlots(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlotDTO> slots = service.getAvailableSlots(date);
        return ResponseEntity.ok(new GeneralResponse<>("Available slots fetched", slots));
    }

    @Operation(summary = "Book an appointment slot", description = "Books a specific 60-minute slot if available. If the slot is unavailable or already booked, returns an error.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Appointment booking details", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Booking Example", value = "{\n" + "  \"inviteeName\": \"John Doe\",\n" + "  \"date\": \"2025-08-15\",\n" + "  \"startTime\": \"09:00:00\"\n" + "}"))), responses = {@ApiResponse(responseCode = "200", description = "Appointment booked successfully", content = @Content(schema = @Schema(implementation = AppointmentDTO.class))), @ApiResponse(responseCode = "400", description = "Slot is not available or already booked", content = @Content(examples = {@ExampleObject(name = "Slot Not Available", value = "{ \"message\": \"Selected slot 09:00 on 2025-08-15 is not available.\" }"), @ExampleObject(name = "Slot Already Booked", value = "{ \"message\": \"Slot already booked\" }")}))})
    @PostMapping("/book")
    public ResponseEntity<?> book(@RequestBody @Valid AppointmentRequestDTO request) {
        AppointmentDTO appointment = service.bookAppointment(request);
        return ResponseEntity.ok(new GeneralResponse<>("Appointment booked!", appointment));
    }

    @Operation(summary = "Get all booked appointments", description = "Retrieves a list of all booked appointments in the system.", responses = @ApiResponse(responseCode = "200", description = "Appointments fetched successfully", content = @Content(schema = @Schema(implementation = Appointment.class))))
    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointments() {
        return ResponseEntity.ok(new GeneralResponse<>(null, service.getAllAppointments()));
    }
}
