package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderSearchCriteria;
import az.ingress.hrms.dto.order.OrderDetailResponse;
import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;
import az.ingress.hrms.dto.order.OrderUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse create(OrderRequest request);

    OrderResponse update(
            Integer id,
            OrderUpdateRequest request
    );

    OrderResponse getById(Integer id);

    OrderDetailResponse getDetailById(Integer id);

    PageResponse<OrderDetailResponse> getAllDetails(OrderSearchCriteria criteria, Pageable pageable);

    PageResponse<OrderResponse> getAll(
            OrderSearchCriteria criteria,
            Pageable pageable
    );

    void softDelete(Integer id);

    void restore(Integer id);

    OrderResponse activate(Integer id);

    OrderResponse deactivate(Integer id);

    OrderResponse close(Integer orderId);

    OrderResponse reopen(Integer orderId);
}