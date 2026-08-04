package az.ingress.hrms.controller.auth;

import az.ingress.hrms.dto.status.StatusRequest;
import az.ingress.hrms.dto.status.StatusResponse;
import az.ingress.hrms.service.auth.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statuses")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Status Management",
        description = "APIs for managing statuses"
)
public class StatusController {

    private final StatusService service;

    @PostMapping
    @Operation(
            summary = "Create status",
            description = "Creates a new status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Status created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Status already exists")
    })
    public ResponseEntity<StatusResponse> create(
            @Valid @RequestBody StatusRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update status",
            description = "Updates an existing status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Status not found"),
            @ApiResponse(responseCode = "410", description = "Status is deleted"),
            @ApiResponse(responseCode = "409", description = "Status already exists")
    })
    public ResponseEntity<StatusResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,

            @Valid @RequestBody StatusRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get status by ID",
            description = "Returns an active status by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status found"),
            @ApiResponse(responseCode = "404", description = "Status not found"),
            @ApiResponse(responseCode = "410", description = "Status is deleted")
    })
    public ResponseEntity<StatusResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all statuses",
            description = "Returns all active statuses."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Statuses retrieved successfully"
    )
    public ResponseEntity<List<StatusResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete status",
            description = "Soft deletes a status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Status not found"),
            @ApiResponse(responseCode = "410", description = "Status is already deleted")
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
            summary = "Restore status",
            description = "Restores a soft-deleted status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status restored successfully"),
            @ApiResponse(responseCode = "400", description = "Status is not deleted"),
            @ApiResponse(responseCode = "404", description = "Status not found")
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