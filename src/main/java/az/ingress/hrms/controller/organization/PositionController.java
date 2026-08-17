package az.ingress.hrms.controller.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.position.PositionRequest;
import az.ingress.hrms.dto.position.PositionResponse;
import az.ingress.hrms.service.organization.PositionService;
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
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Position Management",
        description = "APIs for managing organizational job positions"
)
public class PositionController {

    private final PositionService service;

    @PostMapping
    @Operation(
            summary = "Create position",
            description = "Creates a new organizational position with a unique name."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Position created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "409", description = "Position with the given name already exists")
    })
    public ResponseEntity<PositionResponse> create(
            @Valid @RequestBody PositionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update position",
            description = "Updates an existing position name and details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Position updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Position not found"),
            @ApiResponse(responseCode = "409", description = "Position with the updated name already exists"),
            @ApiResponse(responseCode = "410", description = "Position is deleted")
    })
    public ResponseEntity<PositionResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody PositionRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get position by ID",
            description = "Returns an active position record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Position found"),
            @ApiResponse(responseCode = "404", description = "Position not found"),
            @ApiResponse(responseCode = "410", description = "Position is deleted")
    })
    public ResponseEntity<PositionResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all positions",
            description = "Returns a list of all positions."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Positions retrieved successfully"
    )
    public ResponseEntity<PageResponse<PositionResponse>> getAll(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize) {
        return ResponseEntity.ok(service.getAll(pageNo,pageSize));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate position",
            description = "Changes status of a position record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Position activated successfully"),
            @ApiResponse(responseCode = "404", description = "Position not found"),
            @ApiResponse(responseCode = "410", description = "Position is deleted")
    })
    public ResponseEntity<PositionResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate position",
            description = "Changes status of a position record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Position deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Position not found"),
            @ApiResponse(responseCode = "410", description = "Position is deleted")
    })
    public ResponseEntity<PositionResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete position",
            description = "Soft deletes a position record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Position deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Position not found"),
            @ApiResponse(responseCode = "410", description = "Position is deleted")
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
            summary = "Restore position",
            description = "Restores a soft-deleted position record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Position restored successfully"),
            @ApiResponse(responseCode = "400", description = "Position is not deleted"),
            @ApiResponse(responseCode = "404", description = "Position not found")
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