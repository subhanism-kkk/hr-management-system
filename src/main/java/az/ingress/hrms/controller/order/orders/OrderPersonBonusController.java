//package az.ingress.hrms.controller.order;
//
//import az.ingress.hrms.dto.orderPersonBonus.CreateOrderPersonBonusRequest;
//import az.ingress.hrms.dto.criteria.OrderPersonBonusSearchCriteria;
//import az.ingress.hrms.dto.orderPersonBonus.UpdateOrderPersonBonusRequest;
//import az.ingress.hrms.dto.orderPersonBonus.OrderPersonBonusResponse;
//import az.ingress.hrms.dto.common.PageResponse;
//import az.ingress.hrms.service.order.OrderPersonBonusService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@RestController
//@RequestMapping("/api/v1/order-bonuses")
//@RequiredArgsConstructor
//@Validated
//@Tag(
//        name = "Order Person Bonus Management",
//        description = "APIs for managing employee bonus orders and salary calculations"
//)
//public class OrderPersonBonusController {
//
//    private final OrderPersonBonusService service;
//
//    @PostMapping
//    @Operation(
//            summary = "Create order person bonus",
//            description = "Creates a new bonus record under an order for an employee."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Bonus record created successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., start date before order date, invalid end date)"),
//            @ApiResponse(responseCode = "404", description = "Related resource not found (Order, Person, Bonus Type)"),
//            @ApiResponse(responseCode = "410", description = "Related resource is deleted")
//    })
//    public ResponseEntity<OrderPersonBonusResponse> create(
//            @Valid @RequestBody CreateOrderPersonBonusRequest request
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(service.create(request));
//    }
//
//    @PutMapping("/{id}")
//    @Operation(
//            summary = "Update order person bonus",
//            description = "Updates details of an existing order person bonus record."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Bonus record updated successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error (e.g., start date before order date)"),
//            @ApiResponse(responseCode = "404", description = "Bonus record or related resource not found"),
//            @ApiResponse(responseCode = "410", description = "Bonus record or related resource is deleted")
//    })
//    public ResponseEntity<OrderPersonBonusResponse> update(
//            @PathVariable
//            @Positive(message = "ID must be a positive number")
//            Long id,
//            @Valid @RequestBody UpdateOrderPersonBonusRequest request
//    ) {
//        return ResponseEntity.ok(service.update(id, request));
//    }
//
//    @GetMapping("/{id}")
//    @Operation(
//            summary = "Get order person bonus by ID",
//            description = "Returns an active order person bonus record by its ID."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Bonus record found"),
//            @ApiResponse(responseCode = "404", description = "Bonus record not found"),
//            @ApiResponse(responseCode = "410", description = "Bonus record is deleted")
//    })
//    public ResponseEntity<OrderPersonBonusResponse> getById(
//            @PathVariable
//            @Positive(message = "ID must be a positive number")
//            Long id
//    ) {
//        return ResponseEntity.ok(service.getById(id));
//    }
//
//    @GetMapping
//    @Operation(
//            summary = "Get all order person bonuses",
//            description = "Retrieves bonus records with filtering by keyword (reason, bonus type name, person name), person ID, order ID, bonus type ID, calculation type, and start date range."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Bonus records retrieved successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
//    })
//    public ResponseEntity<PageResponse<OrderPersonBonusResponse>> getAll(
//            OrderPersonBonusSearchCriteria criteria,
//            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
//    ) {
//        return ResponseEntity.ok(service.getAll(criteria, pageable));
//    }
//
//    @GetMapping("/total-salary")
//    @Operation(
//            summary = "Calculate total salary",
//            description = "Calculates total salary for a person on a specific date, factoring in base appointment salary and active fixed/percentage bonuses."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Total salary calculated successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
//            @ApiResponse(responseCode = "404", description = "Active appointment not found for the specified person")
//    })
//    public ResponseEntity<BigDecimal> calculateTotalSalary(
//            @RequestParam
//            @NotNull(message = "Person ID is required")
//            @Positive(message = "Person ID must be a positive number")
//            Integer personId,
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate asOfDate
//    ) {
//        LocalDate date = (asOfDate != null) ? asOfDate : LocalDate.now();
//        return ResponseEntity.ok(service.calculateTotalSalary(personId, date));
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(
//            summary = "Soft delete order person bonus",
//            description = "Soft deletes a bonus record."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "Bonus record deleted successfully"),
//            @ApiResponse(responseCode = "404", description = "Bonus record not found"),
//            @ApiResponse(responseCode = "410", description = "Bonus record is already deleted")
//    })
//    public ResponseEntity<Void> softDelete(
//            @PathVariable
//            @Positive(message = "ID must be a positive number")
//            Long id
//    ) {
//        service.softDelete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/restore")
//    @Operation(
//            summary = "Restore order person bonus",
//            description = "Restores a soft-deleted bonus record."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "Bonus record restored successfully"),
//            @ApiResponse(responseCode = "400", description = "Bonus record is not deleted"),
//            @ApiResponse(responseCode = "404", description = "Bonus record not found")
//    })
//    public ResponseEntity<Void> restore(
//            @PathVariable
//            @Positive(message = "ID must be a positive number")
//            Long id
//    ) {
//        service.restore(id);
//        return ResponseEntity.noContent().build();
//    }
//}