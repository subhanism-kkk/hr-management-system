package az.ingress.hrms.controller.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.PersonPhotoSearchCriteria;
import az.ingress.hrms.dto.personPhoto.PersonPhotoCreateRequest;
import az.ingress.hrms.dto.personPhoto.PersonPhotoResponse;
import az.ingress.hrms.dto.personPhoto.PersonPhotoUpdateRequest;
import az.ingress.hrms.service.person.PersonPhotoService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/person-photos")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Person Photo Management",
        description = "APIs for managing person profile photos and main photo selection"
)
public class PersonPhotoController {

    private final PersonPhotoService service;

    @PostMapping
    @Operation(
            summary = "Create person photo",
            description = "Uploads/registers a new photo for a person. Demotes existing main photo if this one is set as main."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Person photo created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "409", description = "Person with this photo already exists")
    })
    public ResponseEntity<PersonPhotoResponse> create(
            @Valid @RequestBody PersonPhotoCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update person photo",
            description = "Updates an existing photo record. Handles main photo demotion if set to true."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Person photo not found"),
            @ApiResponse(responseCode = "409", description = "Another photo with this file path already exists"),
            @ApiResponse(responseCode = "410", description = "Photo record is deleted")
    })
    public ResponseEntity<PersonPhotoResponse> update(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id,
            @Valid @RequestBody PersonPhotoUpdateRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get photo by ID",
            description = "Retrieves an active photo record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo record found"),
            @ApiResponse(responseCode = "404", description = "Photo record not found"),
            @ApiResponse(responseCode = "410", description = "Photo record is deleted")
    })
    public ResponseEntity<PersonPhotoResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all photo records",
            description = "Retrieves person photo records with optional search, person ID, main photo flag, status, date filtering, sorting, and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo records retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<PageResponse<PersonPhotoResponse>> getAll(
            PersonPhotoSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @GetMapping("/person/{personId}/main")
    @Operation(
            summary = "Get main photo by Person ID",
            description = "Retrieves the primary active main photo for a specific person ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Main photo record retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Person or main photo not found")
    })
    public ResponseEntity<PersonPhotoResponse> getMainPhoto(
            @PathVariable
            @Positive(message = "Person ID must be a positive number")
            Integer personId
    ) {
        return ResponseEntity.ok(service.getMainPhoto(personId));
    }

    @PatchMapping("/{photoId}/set-main")
    @Operation(
            summary = "Set main photo",
            description = "Sets the designated photo as the main photo for the person and demotes any previous main photo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Main photo set successfully"),
            @ApiResponse(responseCode = "404", description = "Photo not found"),
            @ApiResponse(responseCode = "410", description = "Photo is deleted")
    })
    public ResponseEntity<Void> setMainPhoto(
            @PathVariable
            @Positive(message = "Photo ID must be a positive number")
            Integer photoId
    ) {
        service.setMainPhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Activate person photo",
            description = "Changes status of a photo record to active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo record activated successfully"),
            @ApiResponse(responseCode = "404", description = "Photo record not found"),
            @ApiResponse(responseCode = "410", description = "Photo record is deleted")
    })
    public ResponseEntity<PersonPhotoResponse> activate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate person photo",
            description = "Changes status of a photo record to inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo record deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Photo record not found"),
            @ApiResponse(responseCode = "410", description = "Photo record is deleted")
    })
    public ResponseEntity<PersonPhotoResponse> deactivate(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete person photo",
            description = "Soft deletes a person photo record. Resets main photo flag if applicable."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Photo record soft-deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Photo record not found"),
            @ApiResponse(responseCode = "410", description = "Photo record is already deleted")
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
            summary = "Restore person photo",
            description = "Restores a soft-deleted person photo record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Photo record restored successfully"),
            @ApiResponse(responseCode = "400", description = "Photo record is not deleted"),
            @ApiResponse(responseCode = "404", description = "Photo record not found")
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