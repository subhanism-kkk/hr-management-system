package az.ingress.hrms.service.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderPersonPromotionSearchCriteria;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionResponse;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;
import az.ingress.hrms.entity.order.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrderPersonPromotionService {

    OrderPersonPromotionResponse create(
            Order order,
            OrderPersonPromotionCreateRequest request
    );

    OrderPersonPromotionResponse update(
            Order order,
            OrderPersonPromotionUpdateRequest request
    );

    void activate(Order order);

    void deactivate(Order order);

    void softDelete(Order order);

    void restore(Order order);

    PageResponse<OrderPersonPromotionResponse> getAll(
            OrderPersonPromotionSearchCriteria criteria,
            Pageable pageable
    );

    List<OrderPersonPromotionResponse> getByOrderId(Integer orderId);
}