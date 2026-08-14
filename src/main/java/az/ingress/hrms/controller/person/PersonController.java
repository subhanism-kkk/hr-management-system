package az.ingress.hrms.controller.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import az.ingress.hrms.service.person.PersonService;
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
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Person Management",
        description = "APIs for managing general person entity records"
)
public class PersonController {

    private final PersonService service;

    @PostMapping
    @Operation(
            summary = "Create person",
            description = "Creates a new person entity and sets initial status to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error")
    })
    public ResponseEntity<PersonResponse> create(
            @Valid @RequestBody PersonRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update person",
            description = "Updates an existing person record by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "410", description = "Person is deleted")
    })
    public ResponseEntity<PersonResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody PersonRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get person by ID",
            description = "Retrieves an active person record by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person record found"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "410", description = "Person is deleted")
    })
    public ResponseEntity<PersonResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all persons",
            description = "Retrieves a list of all active person records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Person records retrieved successfully"
    )
    public ResponseEntity<PageResponse<PersonResponse>> getAll(
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

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate person",
            description = "Changes status of a person record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person activated successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "410", description = "Person is deleted")
    })
    public ResponseEntity<PersonResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate person",
            description = "Changes status of a person record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "410", description = "Person is deleted")
    })
    public ResponseEntity<PersonResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete person",
            description = "Soft deletes a person record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Person soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "410", description = "Person is already deleted")
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
            summary = "Restore person",
            description = "Restores a soft-deleted person record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Person record restored successfully"),
            @ApiResponse(responseCode = "400", description = "Person is not deleted"),
            @ApiResponse(responseCode = "404", description = "Person not found")
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