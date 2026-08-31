package az.ingress.hrms.controller.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderSearchCriteria;
import az.ingress.hrms.dto.order.OrderDetailResponse;
import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;
import az.ingress.hrms.dto.order.OrderUpdateRequest;
import az.ingress.hrms.service.order.OrderService;
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

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Management",
        description = "APIs for managing orders and their associated line items"
)
public class OrderController {

    private final OrderService service;

    @PostMapping
    @Operation(summary = "Create order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or item validation failure"),
            @ApiResponse(responseCode = "404", description = "Order type or entity resource not found")
    })
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order",
            description = "Updates an existing order's date and processes updates for its line items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload structure or invalid date constraints"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is deleted")
    })
    public ResponseEntity<OrderResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody OrderUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order header by id",
            description = "Returns active order metadata by ID without loading nested items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is deleted")
    })
    public ResponseEntity<OrderResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/details")
    @Operation(
            summary = "Get detailed order by id",
            description = "Returns an active order by ID along with its full polymorphic child item payload."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found with child details"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is deleted")
    })
    public ResponseEntity<OrderDetailResponse> getDetailById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getDetailById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all order headers",
            description = "Returns a paginated list of orders (headers only)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully"
    )
    public ResponseEntity<PageResponse<OrderResponse>> getAll(
            OrderSearchCriteria criteria,
            @PageableDefault(sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @GetMapping("/details")
    @Operation(
            summary = "Get all order details",
            description = "Returns a paginated list of orders including their nested child item details."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Orders with details retrieved successfully"
    )
    public ResponseEntity<PageResponse<OrderDetailResponse>> getAllDetails(
            OrderSearchCriteria criteria,
            @PageableDefault(sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAllDetails(criteria, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order",
            description = "Soft deletes an order and cascade soft-deletes all child line items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is already deleted")
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
            summary = "Restore order",
            description = "Restores a soft-deleted order and all its child line items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order restored successfully"),
            @ApiResponse(responseCode = "400", description = "Order is not deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found")
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
            summary = "Activate order",
            description = "Changes the status of an order and its associated items to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order activated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is deleted")
    })
    public ResponseEntity<OrderResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate order",
            description = "Changes the status of an order and its associated items to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is deleted")
    })
    public ResponseEntity<OrderResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }
    @PatchMapping("/{id}/close")
    @Operation(
            summary = "Close order",
            description = "Closes an active order and updates state for relevant child items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order closed successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is deleted")
    })
    public ResponseEntity<OrderResponse> close(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.close(id));
    }

    @PatchMapping("/{id}/reopen")
    @Operation(
            summary = "Reopen order",
            description = "Reopens a closed order and updates state for relevant child items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order reopened successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is deleted")
    })
    public ResponseEntity<OrderResponse> reopen(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.reopen(id));
    }
}