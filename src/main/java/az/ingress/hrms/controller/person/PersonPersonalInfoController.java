package az.ingress.hrms.controller.person;

import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoCreateRequest;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoResponse;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoUpdateRequest;
import az.ingress.hrms.service.person.PersonPersonalInfoService;
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
@RequestMapping("/api/v1/person-personal-info")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Person Personal Info Management",
        description = "APIs for managing individual personal identification details and FIN codes"
)
public class PersonPersonalInfoController {

    private final PersonPersonalInfoService service;

    @PostMapping
    @Operation(
            summary = "Create person personal info",
            description = "Creates a new personal information record for a person. Checks for person-level existence and unique FIN code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Personal information created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "409", description = "Person already has personal info OR FIN code already exists")
    })
    public ResponseEntity<PersonPersonalInfoResponse> create(
            @Valid @RequestBody PersonPersonalInfoCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update person personal info",
            description = "Updates an existing personal information record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personal information updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Personal information record not found"),
            @ApiResponse(responseCode = "410", description = "Personal information record is deleted")
    })
    public ResponseEntity<PersonPersonalInfoResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody PersonPersonalInfoUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get personal info by ID",
            description = "Retrieves an active personal information record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personal information record found"),
            @ApiResponse(responseCode = "404", description = "Personal information record not found"),
            @ApiResponse(responseCode = "410", description = "Personal information record is deleted")
    })
    public ResponseEntity<PersonPersonalInfoResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all personal info records",
            description = "Retrieves a list of all active personal information records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Personal information records retrieved successfully"
    )
    public ResponseEntity<Page<PersonPersonalInfoResponse>> getAll(
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
            summary = "Activate personal info",
            description = "Changes status of a personal information record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personal information record activated successfully"),
            @ApiResponse(responseCode = "404", description = "Personal information record not found"),
            @ApiResponse(responseCode = "410", description = "Personal information record is deleted")
    })
    public ResponseEntity<PersonPersonalInfoResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate personal info",
            description = "Changes status of a personal information record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personal information record deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Personal information record not found"),
            @ApiResponse(responseCode = "410", description = "Personal information record is deleted")
    })
    public ResponseEntity<PersonPersonalInfoResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete personal info",
            description = "Soft deletes a personal information record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Personal information record soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Personal information record not found"),
            @ApiResponse(responseCode = "410", description = "Personal information record is already deleted")
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
            summary = "Restore personal info",
            description = "Restores a soft-deleted personal information record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Personal information record restored successfully"),
            @ApiResponse(responseCode = "400", description = "Personal information record is not deleted"),
            @ApiResponse(responseCode = "404", description = "Personal information record not found")
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