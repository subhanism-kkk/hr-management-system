package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonTransferSearchCriteria;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferCreateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferResponse;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrderPersonTransferService {

    OrderPersonTransferResponse create(
            Order order,
                                       OrderPersonTransferCreateRequest request);

    OrderPersonTransferResponse update(
            Order order,
            OrderPersonTransferUpdateRequest request);

    List<OrderPersonTransferResponse> getByOrderId(Integer orderId);

    PageResponse<OrderPersonTransferResponse> getAll(OrderPersonTransferSearchCriteria criteria, Pageable pageable);

    void softDelete(Order order);

    void restore(Order order);

    void activate(Order order);

    void deactivate(Order order);
}