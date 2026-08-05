package az.ingress.hrms.controller.organization;

import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.dto.structure.StructureResponse;
import az.ingress.hrms.service.organization.StructureService;
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
@RequestMapping("/api/v1/structures")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Structure Management",
        description = "APIs for managing hierarchical organizational structures"
)
public class StructureController {

    private final StructureService service;

    @PostMapping
    @Operation(
            summary = "Create organizational structure",
            description = "Creates a new structure entity. Validates against duplicate names and self-parenting."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Structure created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload, validation error, or structure attempting to be its own parent"),
            @ApiResponse(responseCode = "404", description = "Parent structure not found"),
            @ApiResponse(responseCode = "409", description = "Structure with the given name already exists"),
            @ApiResponse(responseCode = "410", description = "Parent structure is deleted")
    })
    public ResponseEntity<StructureResponse> create(
            @Valid @RequestBody StructureRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update organizational structure",
            description = "Updates an existing structure. Prevents self-parenting and multi-level circular hierarchy dependency loops."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Structure updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload, self-parenting attempt, or circular hierarchy loop detected"),
            @ApiResponse(responseCode = "404", description = "Structure or target parent structure not found"),
            @ApiResponse(responseCode = "409", description = "Structure with the updated name already exists"),
            @ApiResponse(responseCode = "410", description = "Structure or parent structure is deleted")
    })
    public ResponseEntity<StructureResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody StructureRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get structure by ID",
            description = "Returns an active structure record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Structure found"),
            @ApiResponse(responseCode = "404", description = "Structure not found"),
            @ApiResponse(responseCode = "410", description = "Structure is deleted")
    })
    public ResponseEntity<StructureResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all structures",
            description = "Returns a list of all active structures."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Structures retrieved successfully"
    )
    public ResponseEntity<Page<StructureResponse>> getAll(
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

    @GetMapping("/roots")
    @Operation(
            summary = "Get top-level (root) structures",
            description = "Returns all root structures that do not have a parent structure."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Root structures retrieved successfully"
    )
    public ResponseEntity<Page<StructureResponse>> getRootStructures(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize
    ) {
        return ResponseEntity.ok(service.getRootStructures(pageNo, pageSize));
    }

    @GetMapping("/{parentId}/children")
    @Operation(
            summary = "Get child structures",
            description = "Returns all direct child structures under a given parent structure ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Child structures retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parent structure not found"),
            @ApiResponse(responseCode = "410", description = "Parent structure is deleted")
    })
    public ResponseEntity<Page<StructureResponse>> getChildren(
            @PathVariable
            @Positive(message = "Parent ID must be a positive number")
            Integer parentId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize
    ) {
        return ResponseEntity.ok(service.getChildren(parentId, pageNo, pageSize));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate structure",
            description = "Changes status of a structure record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Structure activated successfully"),
            @ApiResponse(responseCode = "404", description = "Structure not found"),
            @ApiResponse(responseCode = "410", description = "Structure is deleted")
    })
    public ResponseEntity<StructureResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate structure",
            description = "Changes status of a structure record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Structure deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Structure not found"),
            @ApiResponse(responseCode = "410", description = "Structure is deleted")
    })
    public ResponseEntity<StructureResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete structure",
            description = "Soft deletes a structure record if it does not have child structures."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Structure deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete a structure that has child structures"),
            @ApiResponse(responseCode = "404", description = "Structure not found"),
            @ApiResponse(responseCode = "410", description = "Structure is deleted")
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
            summary = "Restore structure",
            description = "Restores a soft-deleted structure record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Structure restored successfully"),
            @ApiResponse(responseCode = "400", description = "Structure is not deleted"),
            @ApiResponse(responseCode = "404", description = "Structure not found")
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