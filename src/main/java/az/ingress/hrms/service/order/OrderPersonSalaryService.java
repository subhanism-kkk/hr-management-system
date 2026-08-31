package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonSalarySearchCriteria;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryResponse;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrderPersonSalaryService {

    OrderPersonSalaryResponse create(Order order,
            OrderPersonSalaryCreateRequest request
    );

    OrderPersonSalaryResponse update(
            Order order,
            OrderPersonSalaryUpdateRequest request
    );

    PageResponse<OrderPersonSalaryResponse> getAll(OrderPersonSalarySearchCriteria criteria, Pageable pageable);

    void softDelete(Order order);

    void restore(Order order);

    void activate(Order order);

    void deactivate(Order order);

    List<OrderPersonSalaryResponse> getByOrderId(Integer orderId);
}