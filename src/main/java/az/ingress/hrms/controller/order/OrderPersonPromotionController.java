package az.ingress.hrms.controller.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonPromotionSearchCriteria;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionResponse;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;
import az.ingress.hrms.service.order.OrderPersonPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-promotions")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Person Promotion Management",
        description = "APIs for managing employee promotion orders"
)
public class OrderPersonPromotionController {

    private final OrderPersonPromotionService service;

    @PostMapping
    @Operation(
            summary = "Create order person promotion",
            description = "Creates a new person promotion record under a promotion order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Promotion record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., non-PRO order type, identical new position, position mismatch with active appointment, effective date before order date)"),
            @ApiResponse(responseCode = "404", description = "Related resource not found (Order, Person, Position, Active Appointment)"),
            @ApiResponse(responseCode = "410", description = "Related resource is deleted")
    })
    public ResponseEntity<OrderPersonPromotionResponse> create(
            @Valid @RequestBody OrderPersonPromotionCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order person promotion",
            description = "Updates an existing order person promotion record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promotion record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., identical new position, effective date before order date)"),
            @ApiResponse(responseCode = "404", description = "Promotion record or related resource not found"),
            @ApiResponse(responseCode = "410", description = "Promotion record or related resource is deleted")
    })
    public ResponseEntity<OrderPersonPromotionResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody OrderPersonPromotionUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order person promotion by ID",
            description = "Returns an active order person promotion record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promotion record found"),
            @ApiResponse(responseCode = "404", description = "Promotion record not found"),
            @ApiResponse(responseCode = "410", description = "Promotion record is deleted")
    })
    public ResponseEntity<OrderPersonPromotionResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all order person promotions",
            description = "Retrieves order person promotion records with optional search, person ID, order ID, old/new position IDs or names, effective date range, status, created date filtering, sorting, and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promotion records retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<PageResponse<OrderPersonPromotionResponse>> getAll(
            OrderPersonPromotionSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate order person promotion",
            description = "Changes the status of a promotion record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promotion record activated successfully"),
            @ApiResponse(responseCode = "404", description = "Promotion record not found"),
            @ApiResponse(responseCode = "410", description = "Promotion record is deleted")
    })
    public ResponseEntity<OrderPersonPromotionResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate order person promotion",
            description = "Changes the status of a promotion record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promotion record deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Promotion record not found"),
            @ApiResponse(responseCode = "410", description = "Promotion record is deleted")
    })
    public ResponseEntity<OrderPersonPromotionResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order person promotion",
            description = "Soft deletes a promotion record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promotion record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Promotion record not found"),
            @ApiResponse(responseCode = "410", description = "Promotion record is deleted")
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
            summary = "Restore order person promotion",
            description = "Restores a soft-deleted promotion record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promotion record restored successfully"),
            @ApiResponse(responseCode = "400", description = "Promotion record is not deleted"),
            @ApiResponse(responseCode = "404", description = "Promotion record not found")
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