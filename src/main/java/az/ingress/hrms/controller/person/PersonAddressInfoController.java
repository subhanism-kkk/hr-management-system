package az.ingress.hrms.controller.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoCreateRequest;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoUpdateRequest;
import az.ingress.hrms.service.person.PersonAddressInfoService;
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
@RequestMapping("/api/v1/person-address-info")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Person Address Info Management",
        description = "APIs for managing address records linked to persons"
)
public class PersonAddressInfoController {

    private final PersonAddressInfoService service;

    @PostMapping
    @Operation(
            summary = "Create person address info",
            description = "Creates a new address record for a person. Checks for duplicate address entries per person."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Address information created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "409", description = "This address already exists for the person")
    })
    public ResponseEntity<PersonAddressInfoResponse> create(
            @Valid @RequestBody PersonAddressInfoCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update person address info",
            description = "Updates an existing address record. Checks for duplicate address entries per person if the address changed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address information updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Address information not found"),
            @ApiResponse(responseCode = "409", description = "This address already exists for the person"),
            @ApiResponse(responseCode = "410", description = "Address information is deleted")
    })
    public ResponseEntity<PersonAddressInfoResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody PersonAddressInfoUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get address info by ID",
            description = "Returns an active person address record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address information found"),
            @ApiResponse(responseCode = "404", description = "Address information not found"),
            @ApiResponse(responseCode = "410", description = "Address information is deleted")
    })
    public ResponseEntity<PersonAddressInfoResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all address records",
            description = "Returns a list of all active person address records."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Address records retrieved successfully"
    )
    public ResponseEntity<PageResponse<PersonAddressInfoResponse>> getAll(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "pageNo cannot be negative")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize cannot exceed 100")
            int pageSize
    ) {
        return ResponseEntity.ok(service.getAll(pageNo,pageSize));
    }

    @GetMapping("/person/{personId}")
    @Operation(
            summary = "Get all address records by Person ID",
            description = "Returns all active address records associated with a specific person ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person address records retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<PageResponse<PersonAddressInfoResponse>> getAllByPerson(
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
        return ResponseEntity.ok(service.getAllByPerson(personId, pageNo,pageSize));
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate address info",
            description = "Changes status of an address record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address information activated successfully"),
            @ApiResponse(responseCode = "404", description = "Address information not found"),
            @ApiResponse(responseCode = "410", description = "Address information is deleted")
    })
    public ResponseEntity<PersonAddressInfoResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate address info",
            description = "Changes status of an address record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Address information deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Address information not found"),
            @ApiResponse(responseCode = "410", description = "Address information is deleted")
    })
    public ResponseEntity<PersonAddressInfoResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete address info",
            description = "Soft deletes a person address record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Address information deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address information not found"),
            @ApiResponse(responseCode = "410", description = "Address information is deleted")
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
            summary = "Restore address info",
            description = "Restores a soft-deleted person address record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Address information restored successfully"),
            @ApiResponse(responseCode = "400", description = "Address information is not deleted"),
            @ApiResponse(responseCode = "404", description = "Address information not found")
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