package az.ingress.hrms.controller.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StaffingPlanSearchCriteria;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanResponse;
import az.ingress.hrms.service.organization.StaffingPlanService;
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
@RequestMapping("/api/v1/staffing-plans")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Staffing Plan Management",
        description = "APIs for managing organizational staffing plans, position capacities, and structure allocations"
)
public class StaffingPlanController {

    private final StaffingPlanService service;


    @GetMapping("/{id}")
    @Operation(
            summary = "Get staffing plan by ID",
            description = "Returns an active staffing plan record by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staffing plan found"),
            @ApiResponse(responseCode = "404", description = "Staffing plan not found"),
            @ApiResponse(responseCode = "410", description = "Staffing plan is deleted")
    })
    public ResponseEntity<StaffingPlanResponse> getById(
            @PathVariable
            @Positive(message = "ID must be a positive number")
            Integer id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all staffing plans with dynamic filters",
            description = "Retrieves a paginated list of staffing plans filtered by structure, position, status, closed flag, date range, or search text."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Staffing plans retrieved successfully"
    )
    public ResponseEntity<PageResponse<StaffingPlanResponse>> getAll(
            StaffingPlanSearchCriteria criteria,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

}