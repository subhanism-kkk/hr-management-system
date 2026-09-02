package az.ingress.hrms.service.organization;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.StaffingPlanSearchCriteria;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanResponse;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StaffingPlanService {

    StaffingPlanResponse create(Order order,
                                StaffingPlanCreateRequest request);

    StaffingPlanResponse update(Order order,
                                StaffingPlanUpdateRequest request);

    List<StaffingPlanResponse> getByOrderId(Integer orderId);

    StaffingPlanResponse getById(Integer id);

    PageResponse<StaffingPlanResponse> getAll(StaffingPlanSearchCriteria criteria, Pageable pageable);

    void close(Integer id);

    void reopen(Integer id);

    void softDelete(Order order);

    void restore(Order order);

    List<StaffingPlanResponse> activate(Order order);

    List<StaffingPlanResponse> deactivate(Order order);

}
