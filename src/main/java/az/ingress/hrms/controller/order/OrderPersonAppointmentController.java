package az.ingress.hrms.controller.order;

import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentCreateRequest;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentResponse;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentUpdateRequest;
import az.ingress.hrms.service.order.OrderPersonAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order-person-appointments")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Person Appointment Management",
        description = "APIs for managing employee appointment orders and active positions"
)
public class OrderPersonAppointmentController {

    private final OrderPersonAppointmentService service;

    @PostMapping
    @Operation(
            summary = "Create order person appointment",
            description = "Creates a new appointment record under an appointment order if capacity allows."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or capacity limit reached"),
            @ApiResponse(responseCode = "409", description = "Person already has an active appointment"),
            @ApiResponse(responseCode = "404", description = "Related resource not found (Order, Person, StaffingPlan)"),
            @ApiResponse(responseCode = "410", description = "Related resource is deleted")
    })
    public ResponseEntity<OrderPersonAppointmentResponse> create(
            @Valid @RequestBody OrderPersonAppointmentCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order person appointment",
            description = "Updates an existing appointment record and verifies staffing plan capacity."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or capacity limit reached"),
            @ApiResponse(responseCode = "404", description = "Appointment or related resource not found"),
            @ApiResponse(responseCode = "410", description = "Appointment or related resource is deleted")
    })
    public ResponseEntity<OrderPersonAppointmentResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody OrderPersonAppointmentUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order person appointment by ID",
            description = "Returns an active appointment record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "410", description = "Appointment is deleted")
    })
    public ResponseEntity<OrderPersonAppointmentResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all order person appointments",
            description = "Returns a paginated list of active appointment records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Appointments retrieved successfully"
    )
    public ResponseEntity<Page<OrderPersonAppointmentResponse>> getAll(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize
    ) {
        return ResponseEntity.ok(service.getAll(pageNo,pageSize));
    }

    @GetMapping("/person/{personId}")
    @Operation(
            summary = "Get order person appointments by Person ID",
            description = "Returns all appointment records associated with a specific person."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "410", description = "Person is deleted")
    })
    public ResponseEntity<Page<OrderPersonAppointmentResponse>> getByPerson(
            @PathVariable
            @Positive(message = "Person ID must be a positive number")
            Integer personId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize
    ) {
        return ResponseEntity.ok(service.getByPerson(personId,pageNo,pageSize));
    }

    @PostMapping("/{id}/dismiss")
    @Operation(
            summary = "Dismiss an active appointment",
            description = "Closes an active appointment with a dismissal order and effective end date."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment dismissed successfully"),
            @ApiResponse(responseCode = "400", description = "Appointment already closed, date before start date, or invalid order type"),
            @ApiResponse(responseCode = "404", description = "Appointment or dismissal order not found"),
            @ApiResponse(responseCode = "410", description = "Appointment or dismissal order is deleted")
    })
    public ResponseEntity<Void> dismiss(
            @PathVariable("id")
            @Positive(message = "Appointment ID must be a positive number")
            Integer appointmentId,
            @RequestParam
            @Positive(message = "Dismissal order ID must be a positive number")
            Integer dismissalOrderId,
            @RequestParam
            @NotNull(message = "Dismissal date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dismissalDate
    ) {
        service.dismiss(appointmentId, dismissalOrderId, dismissalDate);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate order person appointment",
            description = "Changes status of an appointment record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment activated successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "410", description = "Appointment is deleted")
    })
    public ResponseEntity<OrderPersonAppointmentResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate order person appointment",
            description = "Changes status of an appointment record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "410", description = "Appointment is deleted")
    })
    public ResponseEntity<OrderPersonAppointmentResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order person appointment",
            description = "Soft deletes an appointment record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Appointment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "410", description = "Appointment is deleted")
    })
    public ResponseEntity<Void> softDelete(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(
            summary = "Restore order person appointment",
            description = "Restores a soft-deleted appointment record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Appointment restored successfully"),
            @ApiResponse(responseCode = "400", description = "Appointment is not deleted"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<Void> restore(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}