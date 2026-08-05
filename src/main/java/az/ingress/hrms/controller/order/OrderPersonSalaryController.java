package az.ingress.hrms.controller.order;

import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;
import az.ingress.hrms.service.order.OrderPersonSalaryService;
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
@RequestMapping("/api/v1/order-person-salaries")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Order Person Salary Management",
        description = "APIs for managing staffing plan salary adjustment orders"
)
public class OrderPersonSalaryController {

    private final OrderPersonSalaryService service;

    @PostMapping
    @Operation(
            summary = "Create order person salary adjustment",
            description = "Creates a new salary adjustment record under a salary order and updates the staffing plan salary."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Salary record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., non-SAL order type, new salary less than or equal to current salary, effective date before order date)"),
            @ApiResponse(responseCode = "404", description = "Related resource not found (Order, Staffing Plan)"),
            @ApiResponse(responseCode = "410", description = "Related resource is deleted")
    })
    public ResponseEntity<OrderPersonSalaryResponse> create(
            @Valid @RequestBody OrderPersonSalaryCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update order person salary adjustment",
            description = "Updates an existing order person salary record and updates the staffing plan salary."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salary record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., new salary less than or equal to current salary, effective date before order date)"),
            @ApiResponse(responseCode = "404", description = "Salary record or related resource not found"),
            @ApiResponse(responseCode = "410", description = "Salary record or related resource is deleted")
    })
    public ResponseEntity<OrderPersonSalaryResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody OrderPersonSalaryUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order person salary by ID",
            description = "Returns an active order person salary record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salary record found"),
            @ApiResponse(responseCode = "404", description = "Salary record not found"),
            @ApiResponse(responseCode = "410", description = "Salary record is deleted")
    })
    public ResponseEntity<OrderPersonSalaryResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all order person salary adjustments",
            description = "Returns all active order person salary records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Salary records retrieved successfully"
    )
    public ResponseEntity<Page<OrderPersonSalaryResponse>> getAll(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize) {
        return ResponseEntity.ok(service.getAll(pageNo, pageSize));
    }

    @GetMapping("/staffing-plan/{staffingPlanId}")
    @Operation(
            summary = "Get order person salaries by Staffing Plan ID",
            description = "Returns all salary adjustment records associated with a specific staffing plan."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salary records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
    })
    public ResponseEntity<Page<OrderPersonSalaryResponse>> getByStaffingPlan(
            @PathVariable
            @Positive(message = "Staffing Plan ID must be a positive number")
            Integer staffingPlanId,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize
    ) {
        return ResponseEntity.ok(service.getByStaffingPlan(staffingPlanId, pageNo, pageSize));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate order person salary record",
            description = "Changes the status of a salary record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salary record activated successfully"),
            @ApiResponse(responseCode = "404", description = "Salary record not found"),
            @ApiResponse(responseCode = "410", description = "Salary record is deleted")
    })
    public ResponseEntity<OrderPersonSalaryResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate order person salary record",
            description = "Changes the status of a salary record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salary record deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Salary record not found"),
            @ApiResponse(responseCode = "410", description = "Salary record is deleted")
    })
    public ResponseEntity<OrderPersonSalaryResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete order person salary record",
            description = "Soft deletes a salary record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Salary record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Salary record not found"),
            @ApiResponse(responseCode = "410", description = "Salary record is deleted")
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
            summary = "Restore order person salary record",
            description = "Restores a soft-deleted salary record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Salary record restored successfully"),
            @ApiResponse(responseCode = "400", description = "Salary record is not deleted"),
            @ApiResponse(responseCode = "404", description = "Salary record not found")
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