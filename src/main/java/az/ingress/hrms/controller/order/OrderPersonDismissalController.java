package az.ingress.hrms.controller.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonDismissalSearchCriteria;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalCreateRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalResponse;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalUpdateRequest;
import az.ingress.hrms.service.order.OrderPersonDismissalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/order-dismissals")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Person Dismissal Management",
        description = "APIs for managing employee dismissal orders"
)
public class OrderPersonDismissalController {

    private final OrderPersonDismissalService service;

    @PostMapping
    @Operation(
            summary = "Create order person dismissal",
            description = "Creates a new dismissal record under a dismissal order and closes the active appointment."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dismissal record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., non-DIS order type, dismissal date before order date or appointment start date, no active appointment found)"),
            @ApiResponse(responseCode = "404", description = "Related resource not found (Order, Person)"),
            @ApiResponse(responseCode = "410", description = "Related resource is deleted")
    })
    public ResponseEntity<OrderPersonDismissalResponse> create(
            @Valid @RequestBody OrderPersonDismissalCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order person dismissal",
            description = "Updates an existing order person dismissal record and adjusts appointment end date."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dismissal record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., dismissal date before order date or appointment start date)"),
            @ApiResponse(responseCode = "404", description = "Dismissal record or related resource not found"),
            @ApiResponse(responseCode = "410", description = "Dismissal record or related resource is deleted")
    })
    public ResponseEntity<OrderPersonDismissalResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody OrderPersonDismissalUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order person dismissal by ID",
            description = "Returns an active order person dismissal record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dismissal record found"),
            @ApiResponse(responseCode = "404", description = "Dismissal record not found"),
            @ApiResponse(responseCode = "410", description = "Dismissal record is deleted")
    })
    public ResponseEntity<OrderPersonDismissalResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all order person dismissals",
            description = "Retrieves order person dismissal records with optional search, person ID, order ID, dismissal date range, status, created date filtering, sorting, and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dismissal records retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<PageResponse<OrderPersonDismissalResponse>> getAll(
            OrderPersonDismissalSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }


//    @PatchMapping("/{id}/activate")
//    @Operation(
//            summary = "Activate order person dismissal",
//            description = "Changes the status of a dismissal record to active."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Dismissal record activated successfully"),
//            @ApiResponse(responseCode = "404", description = "Dismissal record not found"),
//            @ApiResponse(responseCode = "410", description = "Dismissal record is deleted")
//    })
//    public ResponseEntity<OrderPersonDismissalResponse> activate(
//            @PathVariable
//            @Positive(message = "ID must be a positive number")
//            Integer id
//    ) {
//        return ResponseEntity.ok(service.activate(id));
//    }
//
//    @PatchMapping("/{id}/deactivate")
//    @Operation(
//            summary = "Deactivate order person dismissal",
//            description = "Changes the status of a dismissal record to inactive."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Dismissal record deactivated successfully"),
//            @ApiResponse(responseCode = "404", description = "Dismissal record not found"),
//            @ApiResponse(responseCode = "410", description = "Dismissal record is deleted")
//    })
//    public ResponseEntity<OrderPersonDismissalResponse> deactivate(
//            @PathVariable
//            @Positive(message = "ID must be a positive number")
//            Integer id
//    ) {
//        return ResponseEntity.ok(service.deactivate(id));
//    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order person dismissal",
            description = "Soft deletes a dismissal record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dismissal record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Dismissal record not found"),
            @ApiResponse(responseCode = "410", description = "Dismissal record is deleted")
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
            summary = "Restore order person dismissal",
            description = "Restores a soft-deleted dismissal record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dismissal record restored successfully"),
            @ApiResponse(responseCode = "400", description = "Dismissal record is not deleted"),
            @ApiResponse(responseCode = "404", description = "Dismissal record not found")
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