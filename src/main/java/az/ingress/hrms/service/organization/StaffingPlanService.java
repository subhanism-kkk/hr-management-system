package az.ingress.hrms.service.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StaffingPlanSearchCriteria;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanResponse;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanUpdateRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface StaffingPlanService {

    StaffingPlanResponse create(StaffingPlanCreateRequest request);

    StaffingPlanResponse update(Integer id, StaffingPlanUpdateRequest request);

    StaffingPlanResponse getById(Integer id);

//    PageResponse<StaffingPlanResponse> getByStructure(Integer structureId, int pageNo, int pageSize);
//
//    PageResponse<StaffingPlanResponse> getByPosition(Integer positionId, int pageNo, int pageSize);

    PageResponse<StaffingPlanResponse> getAll(StaffingPlanSearchCriteria criteria, Pageable pageable);

    void close(Integer id);

    void reopen(Integer id);

    void softDelete(Integer id);

    void restore(Integer id);

    StaffingPlanResponse activate(Integer id);

    StaffingPlanResponse deactivate(Integer id);

}
