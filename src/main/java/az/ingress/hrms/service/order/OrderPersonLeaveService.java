package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonLeaveSearchCriteria;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveCreateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveResponse;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrderPersonLeaveService {

    OrderPersonLeaveResponse create(
            Order order,
            OrderPersonLeaveCreateRequest request);

    OrderPersonLeaveResponse update(
            Order order,
            OrderPersonLeaveUpdateRequest request);

    List<OrderPersonLeaveResponse> getByOrderId(Integer orderId);

    PageResponse<OrderPersonLeaveResponse> getAll(OrderPersonLeaveSearchCriteria criteria, Pageable pageable);

    void softDelete(Order order);

    void restore(Order order);

    void activate(Order order);

    void deactivate(Order order);
}