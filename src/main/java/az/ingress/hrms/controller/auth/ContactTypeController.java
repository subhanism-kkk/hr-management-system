package az.ingress.hrms.controller.auth;

import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;
import az.ingress.hrms.service.auth.ContactTypeService;
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
@RequestMapping("/api/v1/contact-types")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Contact Type Management",
        description = "APIs for managing contact types"
)
public class ContactTypeController {

    private final ContactTypeService service;

    @PostMapping
    @Operation(
            summary = "Create contact type",
            description = "Creates a new contact type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contact type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Contact type already exists")
    })
    public ResponseEntity<ContactTypeResponse> create(
            @Valid @RequestBody ContactTypeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update contact type",
            description = "Updates an existing contact type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact type updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Contact type not found"),
            @ApiResponse(responseCode = "410", description = "Contact type is deleted"),
            @ApiResponse(responseCode = "409", description = "Contact type already exists")
    })
    public ResponseEntity<ContactTypeResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,

            @Valid @RequestBody ContactTypeRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get contact type by ID",
            description = "Returns an active contact type by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact type found"),
            @ApiResponse(responseCode = "404", description = "Contact type not found"),
            @ApiResponse(responseCode = "410", description = "Contact type is deleted")
    })
    public ResponseEntity<ContactTypeResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all contact types",
            description = "Returns all active contact types."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Contact types retrieved successfully"
    )
    public ResponseEntity<List<ContactTypeResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete contact type",
            description = "Soft deletes a contact type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contact type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contact type not found"),
            @ApiResponse(responseCode = "410", description = "Contact type is already deleted")
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
            summary = "Restore contact type",
            description = "Restores a previously soft-deleted contact type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contact type restored successfully"),
            @ApiResponse(responseCode = "400", description = "Contact type is not deleted"),
            @ApiResponse(responseCode = "404", description = "Contact type not found")
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