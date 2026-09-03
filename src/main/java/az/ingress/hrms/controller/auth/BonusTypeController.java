package az.ingress.hrms.controller.auth;

import az.ingress.hrms.dto.bonusType.BonusTypeRequest;
import az.ingress.hrms.dto.bonusType.BonusTypeResponse;
import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.BonusTypeSearchCriteria;
import az.ingress.hrms.service.auth.BonusTypeService;
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
@RequestMapping("/api/v1/bonus-types")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Bonus Type Management",
        description = "APIs for managing bonus types"
)
public class BonusTypeController {

    private final BonusTypeService service;

    @PostMapping
    @Operation(
            summary = "Create bonus type",
            description = "Creates a new bonus type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bonus type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Bonus type already exists")
    })
    public ResponseEntity<BonusTypeResponse> create(
            @Valid @RequestBody BonusTypeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update bonus type",
            description = "Updates an existing bonus type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bonus type updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Bonus type not found"),
            @ApiResponse(responseCode = "410", description = "Bonus type is deleted"),
            @ApiResponse(responseCode = "409", description = "Bonus type already exists")
    })
    public ResponseEntity<BonusTypeResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,

            @Valid @RequestBody BonusTypeRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/options")
    @Operation(
            summary = "Get active bonus type options",
            description = "Retrieves active, non-deleted bonus types as a list for select dropdowns."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active options retrieved successfully")
    })
    public ResponseEntity<List<BonusTypeResponse>> getActiveOptions() {
        return ResponseEntity.ok(service.getActiveOptions());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get bonus type by ID",
            description = "Returns an active bonus type by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bonus type found"),
            @ApiResponse(responseCode = "404", description = "Bonus type not found"),
            @ApiResponse(responseCode = "410", description = "Bonus type is deleted")
    })
    public ResponseEntity<BonusTypeResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all bonus types",
            description = "Retrieves bonus types with optional search, name filtering, sorting, and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bonus types retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<PageResponse<BonusTypeResponse>> getAll(
            BonusTypeSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete bonus type",
            description = "Soft deletes a bonus type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bonus type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Bonus type not found"),
            @ApiResponse(responseCode = "410", description = "Bonus type is deleted")
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
            summary = "Restore bonus type",
            description = "Restores a soft-deleted bonus type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bonus type restored successfully"),
            @ApiResponse(responseCode = "400", description = "Bonus type is not deleted"),
            @ApiResponse(responseCode = "404", description = "Bonus type not found")
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
            summary = "Activate bonus type",
            description = "Activates an existing bonus type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bonus type activated successfully"),
            @ApiResponse(responseCode = "404", description = "Bonus type not found")
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
            summary = "Deactivate bonus type",
            description = "Deactivates an existing bonus type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bonus type deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Bonus type not found")
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