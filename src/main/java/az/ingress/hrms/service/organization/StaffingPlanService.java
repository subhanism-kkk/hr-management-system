package az.ingress.hrms.service.organization;

import az.ingress.hrms.dto.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.StaffingPlanResponse;
import az.ingress.hrms.dto.StaffingPlanUpdateRequest;

import java.util.List;

public interface StaffingPlanService {

    StaffingPlanResponse create(StaffingPlanCreateRequest request);

    StaffingPlanResponse update(Integer id, StaffingPlanUpdateRequest request);

    StaffingPlanResponse getById(Integer id);

    List<StaffingPlanResponse> getAll();

    List<StaffingPlanResponse> getByStructure(Integer structureId);

    List<StaffingPlanResponse> getByPosition(Integer positionId);

    void close(Integer id);

    void reopen(Integer id);

    void softDelete(Integer id);

    void restore(Integer id);

}
