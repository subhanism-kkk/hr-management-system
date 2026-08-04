package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;

import java.util.List;

public interface OrderPersonSalaryService {

    OrderPersonSalaryResponse create(
            OrderPersonSalaryCreateRequest request
    );

    OrderPersonSalaryResponse update(
            Integer id,
            OrderPersonSalaryUpdateRequest request
    );

    OrderPersonSalaryResponse getById(Integer id);

    List<OrderPersonSalaryResponse> getAll();

    List<OrderPersonSalaryResponse> getByStaffingPlan(
            Integer staffingPlanId
    );

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonSalaryResponse activate(Integer id);

    OrderPersonSalaryResponse deactivate(Integer id);
}