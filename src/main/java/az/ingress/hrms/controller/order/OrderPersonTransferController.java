package az.ingress.hrms.controller.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferCreateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferResponse;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferUpdateRequest;
import az.ingress.hrms.service.order.OrderPersonTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/order-transfers")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Person Transfer Management",
        description = "APIs for managing employee order person transfers"
)
public class OrderPersonTransferController {

    private final OrderPersonTransferService service;

    @PostMapping
    @Operation(
            summary = "Create order person transfer",
            description = "Creates a new person transfer record under a transfer order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transfer record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., non-TRF order type, capacity full, structure/position mismatch)"),
            @ApiResponse(responseCode = "404", description = "Related resource not found (Order, Person, Structure, Position, Staffing Plan)"),
            @ApiResponse(responseCode = "410", description = "Related resource is deleted")
    })
    public ResponseEntity<OrderPersonTransferResponse> create(
            @Valid @RequestBody OrderPersonTransferCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order person transfer",
            description = "Updates an existing order person transfer record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Transfer record or related resource not found"),
            @ApiResponse(responseCode = "410", description = "Transfer record or related resource is deleted")
    })
    public ResponseEntity<OrderPersonTransferResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody OrderPersonTransferUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order person transfer by ID",
            description = "Returns an active order person transfer record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer record found"),
            @ApiResponse(responseCode = "404", description = "Transfer record not found"),
            @ApiResponse(responseCode = "410", description = "Transfer record is deleted")
    })
    public ResponseEntity<OrderPersonTransferResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all order person transfers",
            description = "Returns all active order person transfer records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Transfer records retrieved successfully"
    )
    public ResponseEntity<PageResponse<OrderPersonTransferResponse>> getAll(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize) {
        return ResponseEntity.ok(service.getAll(pageNo, pageSize));
    }

    @GetMapping("/person/{personId}")
    @Operation(
            summary = "Get order person transfers by Person ID",
            description = "Returns all transfer records associated with a specific person."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "410", description = "Person is deleted")
    })
    public ResponseEntity<PageResponse<OrderPersonTransferResponse>> getByPerson(
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
        return ResponseEntity.ok(service.getByPerson(personId, pageNo, pageSize));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate order person transfer",
            description = "Changes the status of a transfer record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer record activated successfully"),
            @ApiResponse(responseCode = "404", description = "Transfer record not found"),
            @ApiResponse(responseCode = "410", description = "Transfer record is deleted")
    })
    public ResponseEntity<OrderPersonTransferResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate order person transfer",
            description = "Changes the status of a transfer record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer record deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Transfer record not found"),
            @ApiResponse(responseCode = "410", description = "Transfer record is deleted")
    })
    public ResponseEntity<OrderPersonTransferResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order person transfer",
            description = "Soft deletes a transfer record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transfer record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Transfer record not found"),
            @ApiResponse(responseCode = "410", description = "Transfer record is deleted")
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
            summary = "Restore order person transfer",
            description = "Restores a soft-deleted transfer record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transfer record restored successfully"),
            @ApiResponse(responseCode = "400", description = "Transfer record is not deleted"),
            @ApiResponse(responseCode = "404", description = "Transfer record not found")
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