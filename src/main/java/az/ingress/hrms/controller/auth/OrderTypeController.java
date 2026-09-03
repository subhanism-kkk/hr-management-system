package az.ingress.hrms.controller.auth;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderTypeSearchCriteria;
import az.ingress.hrms.dto.orderType.OrderTypeRequest;
import az.ingress.hrms.dto.orderType.OrderTypeResponse;
import az.ingress.hrms.service.auth.OrderTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-types")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Type Management",
        description = "APIs for managing order types"
)
public class OrderTypeController {

    private final OrderTypeService service;

    @PostMapping
    @Operation(
            summary = "Create order type",
            description = "Creates a new order type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Order type already exists")
    })
    public ResponseEntity<OrderTypeResponse> create(
            @Valid @RequestBody OrderTypeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order type",
            description = "Updates an existing order type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order type updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Order type not found"),
            @ApiResponse(responseCode = "410", description = "Order type is deleted"),
            @ApiResponse(responseCode = "409", description = "Order type already exists")
    })
    public ResponseEntity<OrderTypeResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,

            @Valid @RequestBody OrderTypeRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/options")
    @Operation(
            summary = "Get active order type options",
            description = "Retrieves active, non-deleted order types as a list for select dropdowns."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active options retrieved successfully")
    })
    public ResponseEntity<List<OrderTypeResponse>> getActiveOptions() {
        return ResponseEntity.ok(service.getActiveOptions());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order type by ID",
            description = "Returns an active order type by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order type found"),
            @ApiResponse(responseCode = "404", description = "Order type not found"),
            @ApiResponse(responseCode = "410", description = "Order type is deleted")
    })
    public ResponseEntity<OrderTypeResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all order types",
            description = "Retrieves order types with optional search, name, code filtering, sorting, and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order types retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<PageResponse<OrderTypeResponse>> getAll(
            OrderTypeSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order type",
            description = "Soft deletes an order type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order type not found"),
            @ApiResponse(responseCode = "410", description = "Order type is deleted")
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
            summary = "Restore order type",
            description = "Restores a soft-deleted order type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order type restored successfully"),
            @ApiResponse(responseCode = "400", description = "Order type is not deleted"),
            @ApiResponse(responseCode = "404", description = "Order type not found")
    })
    public ResponseEntity<Void> restore(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate order type",
            description = "Activates an existing order type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order type activated successfully"),
            @ApiResponse(responseCode = "404", description = "Order type not found")
    })
    public ResponseEntity<Void> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate order type",
            description = "Deactivates an existing order type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order type deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Order type not found")
    })
    public ResponseEntity<Void> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}