package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;
import org.springframework.data.domain.Page;

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

    Page<OrderPersonSalaryResponse> getAll(int pageNo, int pageSize);

    Page<OrderPersonSalaryResponse> getByStaffingPlan(
            Integer staffingPlanId
            , int pageNo, int pageSize
    );

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonSalaryResponse activate(Integer id);

    OrderPersonSalaryResponse deactivate(Integer id);
}