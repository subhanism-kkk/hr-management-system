package az.ingress.hrms.controller.order;

import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;
import az.ingress.hrms.service.order.OrderService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Management",
        description = "APIs for managing orders"
)
public class OrderController {

    private final OrderService service;


    @PostMapping
    @Operation(
            summary = "Create order"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Order already exists")

    })
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get order by id",
            description = "Return an active order by its id."
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


    @GetMapping
    @Operation(
            summary = "Get all orders",
            description = "Returns a paginated list of active order."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully"
    )
    public ResponseEntity<Page<OrderResponse>> getAll(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize
    ) {
        return ResponseEntity.ok(service.getAll(pageNo, pageSize));
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order",
            description = "Soft deletes an order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "410", description = "Order is deleted")
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
            description = "Restores a soft-deleted order."
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
            description = "Changes the status of an order to active."
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
            description = "Changes the status of an order to inactive."
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

}


