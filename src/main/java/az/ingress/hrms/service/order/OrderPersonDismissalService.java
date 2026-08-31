package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonDismissalSearchCriteria;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalCreateRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalResponse;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonDismissal;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderPersonDismissalService {

    OrderPersonDismissalResponse create(
            Order order,
            OrderPersonDismissalCreateRequest request
    );

    OrderPersonDismissalResponse update(
            Order order,
            OrderPersonDismissalUpdateRequest request
    );

    void activate(Order order);

    void deactivate(Order order);

    void softDelete(Order order);

    void restore(Order order);

    PageResponse<OrderPersonDismissalResponse> getAll(
            OrderPersonDismissalSearchCriteria criteria,
            Pageable pageable
    );

    List<OrderPersonDismissalResponse> getByOrderId(Integer orderId);
}