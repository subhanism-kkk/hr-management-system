package az.ingress.hrms.controller.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StaffingPlanSearchCriteria;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanResponse;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanUpdateRequest;
import az.ingress.hrms.service.organization.StaffingPlanService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/staffing-plans")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Staffing Plan Management",
        description = "APIs for managing organizational staffing plans, position capacities, and structure allocations"
)
public class StaffingPlanController {

    private final StaffingPlanService service;

    @PostMapping
    @Operation(
            summary = "Create staffing plan",
            description = "Creates a new staffing plan for a unique combination of Structure and Position."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Staffing plan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Related resource not found (Structure or Position)"),
            @ApiResponse(responseCode = "409", description = "Staffing plan already exists for this structure and position")
    })
    public ResponseEntity<StaffingPlanResponse> create(
            @Valid @RequestBody StaffingPlanCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update staffing plan",
            description = "Updates an existing staffing plan record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staffing plan updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Staffing plan or related resource not found"),
            @ApiResponse(responseCode = "409", description = "Staffing plan already exists for target structure and position"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
    })
    public ResponseEntity<StaffingPlanResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody StaffingPlanUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get staffing plan by ID",
            description = "Returns an active staffing plan record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staffing plan found"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
    })
    public ResponseEntity<StaffingPlanResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all staffing plans with dynamic filters",
            description = "Retrieves a paginated list of staffing plans filtered by structure, position, status, closed flag, date range, or search text."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Staffing plans retrieved successfully"
    )
    public ResponseEntity<PageResponse<StaffingPlanResponse>> getAll(
            StaffingPlanSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

//    @GetMapping("/structure/{structureId}")
//    @Operation(
//            summary = "Get staffing plans by Structure ID",
//            description = "Returns all staffing plans linked to a specific organizational structure."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Staffing plans retrieved successfully"),
//            @ApiResponse(responseCode = "404", description = "Structure not found")
//    })
//    public ResponseEntity<PageResponse<StaffingPlanResponse>> getByStructure(
//            @PathVariable
//            @Positive(message = "Structure ID must be a positive number")
//            Integer structureId,
//
//            @RequestParam(defaultValue = "0")
//            @Min(value = 0, message = "pageNo cannot be negative")
//            int pageNo,
//
//            @RequestParam(defaultValue = "10")
//            @Min(value = 1, message = "pageSize must be at least 1")
//            @Max(value = 100, message = "pageSize cannot exceed 100")
//            int pageSize
//    ) {
//        return ResponseEntity.ok(service.getByStructure(structureId, pageNo, pageSize));
//    }
//
//    @GetMapping("/position/{positionId}")
//    @Operation(
//            summary = "Get staffing plans by Position ID",
//            description = "Returns all staffing plans linked to a specific job position."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Staffing plans retrieved successfully"),
//            @ApiResponse(responseCode = "404", description = "Position not found")
//    })
//    public ResponseEntity<PageResponse<StaffingPlanResponse>> getByPosition(
//            @PathVariable
//            @Positive(message = "Position ID must be a positive number")
//            Integer positionId,
//
//            @RequestParam(defaultValue = "0")
//            @Min(value = 0, message = "pageNo cannot be negative")
//            int pageNo,
//
//            @RequestParam(defaultValue = "10")
//            @Min(value = 1, message = "pageSize must be at least 1")
//            @Max(value = 100, message = "pageSize cannot exceed 100")
//            int pageSize
//    ) {
//        return ResponseEntity.ok(service.getByPosition(positionId, pageNo, pageSize));
//    }

    @PatchMapping("/{id}/close")
    @Operation(
            summary = "Close staffing plan",
            description = "Marks an open staffing plan as closed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Staffing plan closed successfully"),
            @ApiResponse(responseCode = "400", description = "Staffing plan is already closed"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
    })
    public ResponseEntity<Void> close(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        service.close(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reopen")
    @Operation(
            summary = "Reopen staffing plan",
            description = "Reopens a closed staffing plan."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Staffing plan reopened successfully"),
            @ApiResponse(responseCode = "400", description = "Staffing plan state error"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
    })
    public ResponseEntity<Void> reopen(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        service.reopen(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate staffing plan",
            description = "Changes status of a staffing plan to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staffing plan activated successfully"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
    })
    public ResponseEntity<StaffingPlanResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate staffing plan",
            description = "Changes status of a staffing plan to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staffing plan deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
    })
    public ResponseEntity<StaffingPlanResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete staffing plan",
            description = "Soft deletes a staffing plan record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Staffing plan deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
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
            summary = "Restore staffing plan",
            description = "Restores a soft-deleted staffing plan record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Staffing plan restored successfully"),
            @ApiResponse(responseCode = "400", description = "Staffing plan is not deleted"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found")
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