package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;


public interface OrderPersonSalaryService {

    OrderPersonSalaryResponse create(
            OrderPersonSalaryCreateRequest request
    );

    OrderPersonSalaryResponse update(
            Integer id,
            OrderPersonSalaryUpdateRequest request
    );

    OrderPersonSalaryResponse getById(Integer id);

    PageResponse<OrderPersonSalaryResponse> getAll(int pageNo, int pageSize);

    PageResponse<OrderPersonSalaryResponse> getByStaffingPlan(
            Integer staffingPlanId
            , int pageNo, int pageSize
    );

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonSalaryResponse activate(Integer id);

    OrderPersonSalaryResponse deactivate(Integer id);
}