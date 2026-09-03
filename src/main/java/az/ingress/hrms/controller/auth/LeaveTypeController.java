package az.ingress.hrms.controller.auth;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.LeaveTypeSearchCriteria;
import az.ingress.hrms.dto.leaveType.LeaveTypeCreateRequest;
import az.ingress.hrms.dto.leaveType.LeaveTypeResponse;
import az.ingress.hrms.dto.leaveType.LeaveTypeUpdateRequest;
import az.ingress.hrms.service.auth.LeaveTypeService;
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
@RequestMapping("/api/v1/leave-types")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Leave Type Management",
        description = "APIs for managing leave types"
)
public class LeaveTypeController {

    private final LeaveTypeService service;

    @PostMapping
    @Operation(
            summary = "Create leave type",
            description = "Creates a new leave type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Leave type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or duplicate leave type"),
            @ApiResponse(responseCode = "409", description = "Leave type already exists")
    })
    public ResponseEntity<LeaveTypeResponse> create(
            @Valid @RequestBody LeaveTypeCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update leave type",
            description = "Updates an existing leave type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Leave type not found"),
            @ApiResponse(responseCode = "410", description = "Leave type is deleted")
    })
    public ResponseEntity<LeaveTypeResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,

            @Valid @RequestBody LeaveTypeUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get leave type by ID",
            description = "Returns an active leave type by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type found"),
            @ApiResponse(responseCode = "404", description = "Leave type not found"),
            @ApiResponse(responseCode = "410", description = "Leave type is deleted")
    })
    public ResponseEntity<LeaveTypeResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(
            summary = "Get leave type by code",
            description = "Returns an active leave type by its code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type found"),
            @ApiResponse(responseCode = "404", description = "Leave type not found")
    })
    public ResponseEntity<LeaveTypeResponse> getByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @GetMapping
    @Operation(
            summary = "Get all leave types",
            description = "Retrieves leave types with optional search, code, name, status, date filtering, sorting, and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave types retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<PageResponse<LeaveTypeResponse>> getAll(
            LeaveTypeSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete leave type",
            description = "Soft deletes a leave type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Leave type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Leave type not found"),
            @ApiResponse(responseCode = "400", description = "Leave type is already deleted")
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
            summary = "Restore leave type",
            description = "Restores a soft-deleted leave type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Leave type restored successfully"),
            @ApiResponse(responseCode = "400", description = "Leave type is not deleted"),
            @ApiResponse(responseCode = "404", description = "Leave type not found")
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
            summary = "Activate leave type",
            description = "Activates an inactive leave type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type activated successfully"),
            @ApiResponse(responseCode = "400", description = "Leave type is already active"),
            @ApiResponse(responseCode = "404", description = "Leave type not found"),
            @ApiResponse(responseCode = "410", description = "Leave type is deleted")
    })
    public ResponseEntity<LeaveTypeResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate leave type",
            description = "Deactivates an active leave type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Leave type is already inactive"),
            @ApiResponse(responseCode = "404", description = "Leave type not found"),
            @ApiResponse(responseCode = "410", description = "Leave type is deleted")
    })
    public ResponseEntity<LeaveTypeResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }
}