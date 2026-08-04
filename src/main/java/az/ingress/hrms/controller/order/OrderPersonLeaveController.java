package az.ingress.hrms.controller.order;

import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveCreateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveResponse;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveUpdateRequest;
import az.ingress.hrms.service.order.OrderPersonLeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-person-leaves")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Person Leave Management",
        description = "APIs for managing employee leave orders"
)
public class OrderPersonLeaveController {

    private final OrderPersonLeaveService service;

    @PostMapping
    @Operation(
            summary = "Create order person leave",
            description = "Creates a new person leave record under a leave order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Leave record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., non-LEV order type, invalid date range, overlapping leave, inactive leave type)"),
            @ApiResponse(responseCode = "404", description = "Related resource not found (Order, Person, Leave Type, Active Appointment)"),
            @ApiResponse(responseCode = "410", description = "Related resource is deleted")
    })
    public ResponseEntity<OrderPersonLeaveResponse> create(
            @Valid @RequestBody OrderPersonLeaveCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order person leave",
            description = "Updates an existing order person leave record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., invalid date range, overlapping leave period)"),
            @ApiResponse(responseCode = "404", description = "Leave record or related resource not found"),
            @ApiResponse(responseCode = "410", description = "Leave record or related resource is deleted")
    })
    public ResponseEntity<OrderPersonLeaveResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody OrderPersonLeaveUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order person leave by ID",
            description = "Returns an active order person leave record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave record found"),
            @ApiResponse(responseCode = "404", description = "Leave record not found"),
            @ApiResponse(responseCode = "410", description = "Leave record is deleted")
    })
    public ResponseEntity<OrderPersonLeaveResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all order person leaves",
            description = "Returns all active order person leave records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Leave records retrieved successfully"
    )
    public ResponseEntity<List<OrderPersonLeaveResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/person/{personId}")
    @Operation(
            summary = "Get order person leaves by Person ID",
            description = "Returns all leave records associated with a specific person."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "410", description = "Person is deleted")
    })
    public ResponseEntity<List<OrderPersonLeaveResponse>> getByPerson(
            @PathVariable
            @Positive(message = "Person ID must be a positive number")
            Integer personId
    ) {
        return ResponseEntity.ok(service.getByPerson(personId));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate order person leave",
            description = "Changes the status of a leave record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave record activated successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot activate an expired leave record"),
            @ApiResponse(responseCode = "404", description = "Leave record not found"),
            @ApiResponse(responseCode = "410", description = "Leave record is deleted")
    })
    public ResponseEntity<OrderPersonLeaveResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate order person leave",
            description = "Changes the status of a leave record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave record deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Leave record not found"),
            @ApiResponse(responseCode = "410", description = "Leave record is deleted")
    })
    public ResponseEntity<OrderPersonLeaveResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order person leave",
            description = "Soft deletes a leave record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Leave record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Leave record not found"),
            @ApiResponse(responseCode = "410", description = "Leave record is deleted")
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
            summary = "Restore order person leave",
            description = "Restores a soft-deleted leave record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Leave record restored successfully"),
            @ApiResponse(responseCode = "400", description = "Leave record is not deleted"),
            @ApiResponse(responseCode = "404", description = "Leave record not found")
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