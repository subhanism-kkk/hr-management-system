package az.ingress.hrms.controller.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StructureSearchCriteria;
import az.ingress.hrms.dto.structure.StructureResponse;
import az.ingress.hrms.service.organization.StructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



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
            description = "Retrieves structure records with optional search, parent ID, isClosed status, status code, date filtering, sorting, and pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Structure records retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter")
    })
    public ResponseEntity<PageResponse<StructureResponse>> getAll(
            StructureSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

}