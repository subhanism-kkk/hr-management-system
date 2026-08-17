package az.ingress.hrms.controller.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoCreateRequest;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoResponse;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoUpdateRequest;
import az.ingress.hrms.service.person.PersonContactInfoService;
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
@RequestMapping("/api/v1/person-contacts")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Person Contact Info Management",
        description = "APIs for managing contact details linked to persons"
)
public class PersonContactInfoController {

    private final PersonContactInfoService service;

    @PostMapping
    @Operation(
            summary = "Create person contact info",
            description = "Creates a new contact record for a person. Checks for duplicates by person, contact type, and value."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contact information created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Person or Contact Type not found"),
            @ApiResponse(responseCode = "409", description = "This contact info already exists for this person")
    })
    public ResponseEntity<PersonContactInfoResponse> create(
            @Valid @RequestBody PersonContactInfoCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update person contact info",
            description = "Updates an existing contact record. Checks for duplicate entries if contact value or type has changed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact information updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Contact information or Contact Type not found"),
            @ApiResponse(responseCode = "409", description = "Contact already exists"),
            @ApiResponse(responseCode = "410", description = "Contact information is deleted")
    })
    public ResponseEntity<PersonContactInfoResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody PersonContactInfoUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get contact info by ID",
            description = "Returns an active person contact record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact information found"),
            @ApiResponse(responseCode = "404", description = "Contact information not found"),
            @ApiResponse(responseCode = "410", description = "Contact information is deleted")
    })
    public ResponseEntity<PersonContactInfoResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all contact records",
            description = "Returns a list of all active contact records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Contact records retrieved successfully"
    )
    public ResponseEntity<PageResponse<PersonContactInfoResponse>> getAll(
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

    @GetMapping("/person/{personId}")
    @Operation(
            summary = "Get all contact records by Person ID",
            description = "Returns all active contact records associated with a specific person ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<PageResponse<PersonContactInfoResponse>> getAllByPerson(
            @PathVariable
            @Positive(message = "Person ID must be a positive number")
            Integer personId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize
    ) {
        return ResponseEntity.ok(service.getAllByPerson(personId, pageNo, pageSize));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate contact info",
            description = "Changes status of a contact record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact information activated successfully"),
            @ApiResponse(responseCode = "404", description = "Contact information not found"),
            @ApiResponse(responseCode = "410", description = "Contact information is deleted")
    })
    public ResponseEntity<PersonContactInfoResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate contact info",
            description = "Changes status of a contact record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact information deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Contact information not found"),
            @ApiResponse(responseCode = "410", description = "Contact information is deleted")
    })
    public ResponseEntity<PersonContactInfoResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete contact info",
            description = "Soft deletes a person contact record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contact information deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contact information not found"),
            @ApiResponse(responseCode = "410", description = "Contact information is deleted")
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
            summary = "Restore contact info",
            description = "Restores a soft-deleted person contact record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contact information restored successfully"),
            @ApiResponse(responseCode = "400", description = "Contact information is not deleted"),
            @ApiResponse(responseCode = "404", description = "Contact information not found")
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