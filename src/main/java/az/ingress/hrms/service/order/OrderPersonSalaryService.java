package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonSalarySearchCriteria;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;
import org.springframework.data.domain.Pageable;


public interface OrderPersonSalaryService {

    OrderPersonSalaryResponse create(
            OrderPersonSalaryCreateRequest request
    );

    OrderPersonSalaryResponse update(
            Integer id,
            OrderPersonSalaryUpdateRequest request
    );

    OrderPersonSalaryResponse getById(Integer id);

    PageResponse<OrderPersonSalaryResponse> getAll(OrderPersonSalarySearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

    OrderPersonSalaryResponse activate(Integer id);

    OrderPersonSalaryResponse deactivate(Integer id);
}